package com.cerridan.badmintonscheduler.dagger

import com.cerridan.badmintonscheduler.dialog.AddPlayerFragment
import com.cerridan.badmintonscheduler.dialog.CourtActionsFragment
import com.cerridan.badmintonscheduler.dialog.EndSessionFragment
import com.cerridan.badmintonscheduler.dialog.RemovePlayerFragment
import com.cerridan.badmintonscheduler.fragment.CourtsFragment
import com.cerridan.badmintonscheduler.fragment.PlayersFragment
import com.cerridan.badmintonscheduler.fragment.ReservationFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
  fun inject(addPlayerFragment: AddPlayerFragment)
  fun inject(courtActionsFragment: CourtActionsFragment)
  fun inject(courtsFragment: CourtsFragment)
  fun inject(playersFragment: PlayersFragment)
  fun inject(removePlayerFragment: RemovePlayerFragment)
  fun inject(reservationFragment: ReservationFragment)

  fun inject(endSessionFragment: EndSessionFragment)

  fun viewModelFactory(): DaggerViewModelFactory
}