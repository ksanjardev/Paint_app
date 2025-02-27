package uz.sanjar.androidexam31.utils.navigator

import androidx.navigation.NavDirections

/**
    Created by Sanjar Karimov 4:08 PM 12/4/2024
*/

interface AppNavigator {
    suspend fun navigateTo(resId:Int)
    suspend fun navigateTo(dir: NavDirections)
    suspend fun goBack()
}