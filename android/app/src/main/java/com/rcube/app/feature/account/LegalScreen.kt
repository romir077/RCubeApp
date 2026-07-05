package com.rcube.app.feature.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rcube.app.core.designsystem.component.RcubeTopBar

private data class Section(val heading: String, val body: String)

private val sections = listOf(
    Section(
        "1. About RCube",
        "RCube is the recognition layer for local creators. It helps organizers discover and " +
            "book verified local talent, and helps creators get recognized and earn. By using " +
            "the app you agree to these terms.",
    ),
    Section(
        "2. Accounts & identity",
        "You sign in with your phone number. You must provide accurate details and verify your " +
            "identity (Aadhaar) to book or to be booked. You are responsible for activity on your " +
            "account. One person may hold a single account with both creator and organizer modes.",
    ),
    Section(
        "3. Bookings & payments",
        "Organizers pay the service price upfront; RCube holds the amount and releases the " +
            "creator's earning (net of a platform commission) after the event is completed. " +
            "Contact details are shared only after a booking is confirmed by payment.",
    ),
    Section(
        "4. Creator content",
        "Creators are responsible for the accuracy of their profiles, portfolio media, and " +
            "service claims. Profiles are reviewed before going live and may be declined or " +
            "suspended if they violate these terms.",
    ),
    Section(
        "5. Conduct & safety",
        "Treat others with respect. No fraud, harassment, illegal activity, or off-platform " +
            "circumvention of payments. We may suspend accounts that break these rules.",
    ),
    Section(
        "6. Privacy",
        "We collect only what we need: your phone number, profile details, and identity documents " +
            "for verification. Aadhaar images are used solely to verify identity and are kept " +
            "private. We never sell your personal data. Payment processing is handled by our " +
            "payment partner; card details never touch RCube.",
    ),
    Section(
        "7. Contact",
        "Questions about these terms or your data? Email support@rcube.app.",
    ),
)

@Composable
fun LegalScreen(onBack: () -> Unit) {
    Scaffold(topBar = { RcubeTopBar(title = "Terms & Privacy", onBack = onBack) }) { inner ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = inner.calculateTopPadding() + 4.dp, bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Text(
                    "Last updated: this is a plain-language summary for the MVP.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            sections.forEach { section ->
                item {
                    Text(section.heading, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(section.body, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
