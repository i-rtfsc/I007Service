/*
 * Copyright (c) 2019 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.i007Service.core

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

open class ProduceableSubject<T> : SubjectSupport<T>, Producer<T> {

    private val mSubject: Subject<T>

    init {
        mSubject = createSubject()
    }

    open fun createSubject(): Subject<T> {
        return PublishSubject.create()
    }

    override fun subject(): Observable<T> {
        return mSubject
    }

    override fun produce(data: T) {
        mSubject.onNext(data)
    }

}
