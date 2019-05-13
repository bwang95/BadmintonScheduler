package com.cerridan.badmintonscheduler.dagger

import com.cerridan.badmintonscheduler.api.dialog.AddPlayerFragment
import com.cerridan.badmintonscheduler.fragment.CourtsFragment
import com.cerridan.badmintonscheduler.fragment.PlayersFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
  fun inject(addPlayerFragment: AddPlayerFragment)
  fun inject(courtsFragment: CourtsFragment)
  fun inject(playersFragment: PlayersFragment)
}