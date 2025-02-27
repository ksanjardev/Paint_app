package uz.sanjar.androidexam31.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.sanjar.androidexam31.utils.navigator.AppNavigationDispatcher
import uz.sanjar.androidexam31.utils.navigator.AppNavigator
import uz.sanjar.androidexam31.utils.navigator.AppNavigatorHandler

/**
Created by Sanjar Karimov 4:13 PM 12/4/2024
 */

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {
    @Binds
    fun bindAppNavigator(dispatcher: AppNavigationDispatcher): AppNavigator

    @Binds
    fun bindAppNavigationHandler(dispatcher: AppNavigationDispatcher): AppNavigatorHandler
}