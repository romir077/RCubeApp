package com.rcube.app.feature.account

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rcube.app.core.designsystem.component.RcubeCard
import com.rcube.app.core.designsystem.component.RcubeTopBar
import com.rcube.app.core.designsystem.component.SectionHeader

private data class Faq(val q: String, val a: String)

private val faqs = listOf(
    Faq(
        "How does verification work?",
        "There are two checks: your identity (Aadhaar) and, for creators, a profile review of " +
            "your skill and social links. You can browse and book once your identity is verified.",
    ),
    Faq(
        "When do I get the other person's number?",
        "Contact details are shared only after the creator accepts and the organizer pays. " +
            "Then both of you can coordinate the timing directly.",
    ),
    Faq(
        "How do payments and payouts work?",
        "The organizer pays upfront and RCube holds it safely. After the event is marked " +
            "complete, your earning is released for payout.",
    ),
    Faq(
        "What is the event code (OTP)?",
        "On the event day the organizer shows a code; the creator enters it to start the event. " +
            "It confirms the creator actually showed up.",
    ),
)

@Composable
fun SupportScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(topBar = { RcubeTopBar(title = "Support", onBack = onBack) }) { inner ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = inner.calculateTopPadding() + 4.dp, bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text(
                    "We're here to help. Reach out and we'll get back within a day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                RcubeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 4.dp) {
                    ContactRow(Icons.Filled.Email, "Email us", "support@rcube.app") {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@rcube.app")),
                            )
                        }
                    }
                    ContactRow(Icons.Filled.Phone, "Call us", "+91 90000 00000") {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919000000000")),
                            )
                        }
                    }
                    ContactRow(Icons.AutoMirrored.Filled.Chat, "WhatsApp", "+91 90000 00000") {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919000000000")),
                            )
                        }
                    }
                }
            }
            item { SectionHeader("FAQs") }
            items(faqs) { faq ->
                RcubeCard(modifier = Modifier.fillMaxWidth()) {
                    Text(faq.q, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(faq.a, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(12.dp))
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Text(value, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
    }
}
