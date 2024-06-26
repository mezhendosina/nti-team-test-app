package ru.mezhendosina.shared.ui.aboutItem

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.subscribe
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.mezhendosina.shared.model.cart.CartRepository
import ru.mezhendosina.shared.model.shop.repo.ShopRepository

class DefaultAboutItemComponent(
    private val id: Int,
    private val shopRepository: ShopRepository,
    private val cartRepository: CartRepository,
    private val onBack: () -> Unit,
    private val componentContext: ComponentContext
) : AboutItemComponent, ComponentContext by componentContext {
    private val _model = MutableValue(
        AboutItemComponent.Model(shopRepository.items.value.first { it.id == id })
    )
    override val model: Value<AboutItemComponent.Model> = _model

    init {
        shopRepository.items.subscribe(lifecycle) { list ->
            val item = list.first { it.id == id }
            _model.update {
                AboutItemComponent.Model(item)
            }
        }
    }

    override fun onBack() = onBack.invoke()

    override fun onItemCountChanges(count: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            shopRepository.updateCount(
                model.value.aboutItem.updateCount(count)
            )
        }
    }
}
