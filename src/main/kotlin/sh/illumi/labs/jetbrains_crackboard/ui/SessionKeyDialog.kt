package sh.illumi.labs.jetbrains_crackboard.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import sh.illumi.labs.jetbrains_crackboard.CrackBoardSettings
import java.awt.Color
import java.awt.Desktop
import java.awt.GridLayout
import java.io.IOException
import java.net.URISyntaxException
import javax.swing.*
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener

class SessionKeyDialog : DialogWrapper(true) {
    private val panel: JPanel
    private val label: JLabel
    private val input: JTextField
    private val link: LinkPane
    init {
        title = "CrackBoard Session Key"
        setOKButtonText("Save")
        panel = JPanel()
        panel.layout = GridLayout(0, 1)
        label = JLabel("Enter your CrackBoard session key:", JLabel.CENTER)
        panel.add(label)
        input = JTextField(36)
        panel.add(input)
        link = LinkPane(service<CrackBoardSettings>().state.baseUrl!!)
        panel.add(link)

        init()
    }

    override fun createCenterPanel(): JComponent = panel

    override fun doValidate(): ValidationInfo? = null

    public override fun doOKAction() {
        service<CrackBoardSettings>().state.sessionKey = input.getText()
        super.doOKAction()
    }

    fun promptForApiKey(): String {
        input.text = service<CrackBoardSettings>().state.sessionKey
        this.show()
        return input.text
    }

    internal class LinkPane(private val url: String) : JTextPane() {
        init {
            this.isEditable = false
            this.addHyperlinkListener(UrlHyperlinkListener())
            this.contentType = "text/html"
            this.background = Color(0, 0, 0, 0)
            this.text = url
        }

        private class UrlHyperlinkListener : HyperlinkListener {
            override fun hyperlinkUpdate(event: HyperlinkEvent) {
                if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(event.url.toURI())
                    } catch (e: IOException) {
                        throw RuntimeException("Can't open URL", e)
                    } catch (e: URISyntaxException) {
                        throw RuntimeException("Can't open URL", e)
                    }
                }
            }
        }

        override fun setText(text: String) {
            super.setText("<html><body style=\"text-align:center;\"><a href=\"$url\">$text</a></body></html>")
        }
    }
}
