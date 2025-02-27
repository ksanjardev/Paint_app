package uz.sanjar.androidexam31.utils.navigator

import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Created by Sanjar Karimov 4:05 PM 12/4/2024*/

@Singleton
class AppNavigationDispatcher @Inject constructor() : AppNavigator, AppNavigatorHandler {
    override val navigationStack = MutableSharedFlow<NavigationArgs>()

    override suspend fun navigateTo(resId: Int) {
        navigationStack.emit { navigate(resId) }
    }

    override suspend fun navigateTo(dir: NavDirections) {
        navigationStack.emit { navigate(dir) }
    }

    override suspend fun goBack() {
        navigationStack.emit { navigateUp() }
    }

}