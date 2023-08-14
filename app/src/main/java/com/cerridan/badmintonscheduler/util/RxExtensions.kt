package com.cerridan.badmintonscheduler.util

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3

inline fun <reified T1 : Any, reified T2 : Any> combineLatest(
    source1: ObservableSource<T1>,
    source2: ObservableSource<T2>
): Observable<Pair<T1, T2>> = Observable.combineLatest(source1, source2, BiFunction(::Pair))

inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any> combineLatest(
    source1: ObservableSource<T1>,
    source2: ObservableSource<T2>,
    source3: ObservableSource<T3>
): Observable<Triple<T1, T2, T3>> = Observable.combineLatest(source1, source2, source3, Function3(::Triple))