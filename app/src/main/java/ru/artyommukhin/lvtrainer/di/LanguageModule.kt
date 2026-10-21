package ru.artyommukhin.lvtrainer.di

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {

  const val DEFAULT_LOCALE = "DEFAULT_LOCALE"

  @Singleton
  @Provides
  @Named(DEFAULT_LOCALE)
  fun provideDefaultLocale(@ApplicationContext appContext: Context): Locale =
      ConfigurationCompat.getLocales(appContext.resources.configuration)[0] ?: Locale.getDefault()
}
