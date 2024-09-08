package sh.illumi.labs.jetbrains_crackboard

import com.intellij.openapi.components.*

@Service
@State(
    name = "CrackBoardSettings",
    storages = [Storage("CrackBoardPlugin.xml")]
)
class CrackBoardSettings : SimplePersistentStateComponent<CrackBoardSettingsState>(CrackBoardSettingsState())

class CrackBoardSettingsState : BaseState() {
    var sessionKey by string()
    var baseUrl by string("https://crackboard.dev")
}