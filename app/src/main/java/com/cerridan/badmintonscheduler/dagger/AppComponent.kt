package com.cerridan.badmintonscheduler.dagger

import com.cerridan.badmintonscheduler.fragment.PlayersFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
  fun inject(playersFragment: PlayersFragment)
}