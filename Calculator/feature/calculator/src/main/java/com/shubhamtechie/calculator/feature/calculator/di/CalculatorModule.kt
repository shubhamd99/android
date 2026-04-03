package com.shubhamtechie.calculator.feature.calculator.di

import com.shubhamtechie.calculator.core.common.ExpressionParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalculatorModule {
    
    @Provides
    @Singleton
    fun provideExpressionParser(): ExpressionParser = ExpressionParser()
}
