/*
 * Copyright (C) 2021 The Android Open Source Project.
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
package com.he1io.s4cproject.data.local

import android.app.Application

class S4CApplication : Application(){
    /*
    Usa el delegado lazy para que la instancia database se cree de forma diferida cuando necesites o consultes
    la referencia por primera vez (en lugar de hacerlo cuando se inicie la app).
    Esta acción creará la base de datos (la base de datos física en el disco) en el primer acceso.
     */
    val database: SocialActionRoomDatabase by lazy { SocialActionRoomDatabase.getDatabase((this)) }
}
