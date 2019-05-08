package com.cerridan.badmintonscheduler.model

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.Date

class DataModel {
  companion object {
    const val FILE_NAME = "badminton_scheduler_data_model.json"
  }

  @SerializedName("courts") private val courts = mutableListOf<Court>()
  @SerializedName("players") private val players = mutableListOf<Player>()
  @SerializedName("sessions") private val sessions = mutableListOf<Session>()

  @Transient private val courtsSubject = BehaviorSubject.create<List<Court>>()
  @Transient private val playersSubject = BehaviorSubject.create<List<Player>>()
  @Transient private val sessionsSubject = BehaviorSubject.create<List<Session>>()

  val observableCourts: Observable<List<Court>> get() = courtsSubject
  val observablePlayers: Observable<List<Player>> get() = playersSubject
  val observableSessions: Observable<List<Session>> get() = sessionsSubject

  fun cullExpiredSessions() {
    val now = Date()
    sessions.removeAll { session -> session.endsAt.before(now) }
    sessionsSubject.onNext(sessions)
  }

  fun addPlayer(player: Player): Boolean =
      if (players.any { p -> p.name == player.name }) {
        false
      } else {
        players.add(player)
        playersSubject.onNext(players)
        true
      }
}