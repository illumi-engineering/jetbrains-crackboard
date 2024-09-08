package sh.illumi.labs.crackboardidea

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State

@Service
@State(name = "CrackBoardSettings")
class CrackBoardSettings : SimplePersistentStateComponent<CrackBoardSettingsState>(CrackBoardSettingsState())

class CrackBoardSettingsState : BaseState() {
    var sessionKey by string()
    var baseUrl by string("https://crackboard.dev")
}