package org.luxons.sevenwonders.ui

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.luxons.sevenwonders.ui.components.application
import org.luxons.sevenwonders.ui.redux.SwState
import org.luxons.sevenwonders.ui.redux.configureStore
import org.luxons.sevenwonders.ui.redux.sagas.SagaManager
import org.luxons.sevenwonders.ui.redux.sagas.rootSaga
import org.w3c.dom.Element
import react.dom.*
import react.redux.provider
import redux.RAction
import redux.Store
import redux.WrapperAction
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.onload = {
        val rootElement = document.getElementById("root")
        if (rootElement != null) {
            initializeAndRender(rootElement)
        } else {
            console.error("Element with ID 'root' was not found, cannot bootstrap react app")
        }
    }
}

private fun initializeAndRender(rootElement: Element) {
    val store = initRedux()

    render(rootElement) {
        provider(store) {
            application()
        }
    }
}

private fun initRedux(): Store<SwState, RAction, WrapperAction> {
    val sagaManager = SagaManager<SwState, RAction, WrapperAction>()
    val store = configureStore(sagaManager = sagaManager)
    GlobalScope.promise {
        sagaManager.launchSaga(this) {
            rootSaga()
        }
    }
    return store
}
