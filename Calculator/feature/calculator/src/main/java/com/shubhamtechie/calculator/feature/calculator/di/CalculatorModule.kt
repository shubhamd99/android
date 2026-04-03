package com.shubhamtechie.calculator.feature.calculator.di

import com.shubhamtechie.calculator.core.common.DefaultExpressionParser
import com.shubhamtechie.calculator.core.common.ExpressionParser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class CalculatorModule {

    @Binds
    @ViewModelScoped
    abstract fun bindExpressionParser(impl: DefaultExpressionParser): ExpressionParser
}
