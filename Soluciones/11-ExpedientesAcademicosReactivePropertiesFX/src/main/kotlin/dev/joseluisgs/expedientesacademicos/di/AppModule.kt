package dev.joseluisgs.expedientesacademicos.di

import dev.joseluisgs.expedientesacademicos.alumnado.repositories.AlumnosRepository
import dev.joseluisgs.expedientesacademicos.alumnado.repositories.AlumnosRepositoryImpl
import dev.joseluisgs.expedientesacademicos.alumnado.services.cache.AlumnosCache
import dev.joseluisgs.expedientesacademicos.alumnado.services.cache.AlumnosCacheImpl
import dev.joseluisgs.expedientesacademicos.alumnado.services.database.AlumnosService
import dev.joseluisgs.expedientesacademicos.alumnado.services.database.AlumnosServiceImpl
import dev.joseluisgs.expedientesacademicos.alumnado.services.storage.*
import dev.joseluisgs.expedientesacademicos.alumnado.viewmodels.ExpedientesViewModel
import dev.joseluisgs.expedientesacademicos.config.AppConfig
import dev.joseluisgs.expedientesacademicos.database.SqlDeLightClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AppConfig)

    singleOf(::SqlDeLightClient)

    singleOf(::AlumnosRepositoryImpl) {
        bind<AlumnosRepository>()
    }

    singleOf(::AlumnosStorageJsonImpl) {
        bind<AlumnosStorageJson>()
    }

    singleOf(::AlumnosStorageZipImpl) {
        bind<AlumnosStorageZip>()
    }

    singleOf(::AlumnosStorageImagesImpl) {
        bind<AlumnosStorageImages>()
    }

    singleOf(::AlumnosStorageImpl) {
        bind<AlumnosStorage>()
    }

    singleOf(::AlumnosCacheImpl) {
        bind<AlumnosCache>()
    }

    singleOf(::AlumnosServiceImpl) {
        bind<AlumnosService>()
    }

    singleOf(::ExpedientesViewModel)
}