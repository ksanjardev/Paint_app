package uz.sanjar.androidexam31.utils.navigator

import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow

/**
Created by Sanjar Karimov 4:09 PM 12/4/2024
 */

typealias NavigationArgs = NavController.() -> Unit

interface AppNavigatorHandler {
    val navigationStack: Flow<NavigationArgs>
}