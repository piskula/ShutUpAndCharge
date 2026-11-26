package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

enum class AmountTone { Positive, Negative, Neutral }

@Composable
fun TransactionListItem(
    title: String,
    subtitle: String,
    amount: String,
    amountTone: AmountTone,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    detail: String? = null,
    badgeIcon: ImageVector? = null,
    onOpenStats: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(AppTheme.colors.primaryContainer, CircleShape),
        ) {
            Icon(imageVector = leadingIcon, tint = AppTheme.colors.onPrimaryContainer, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (badgeIcon != null) {
                    Icon(
                        imageVector = badgeIcon,
                        tint = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp).size(16.dp),
                    )
                }
            }
            Text(text = subtitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amount,
                style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = amountTone.color(),
            )
            if (detail != null) {
                Text(text = detail, style = AppTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
            }
        }
        if (onOpenStats != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onOpenStats),
            ) {
                Icon(imageVector = SuacIcons.Stats, tint = AppTheme.colors.primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun AmountTone.color() = when (this) {
    AmountTone.Positive -> AppTheme.colors.positive
    AmountTone.Negative -> AppTheme.colors.error
    AmountTone.Neutral -> AppTheme.colors.onSurface
}

fun amountToneOf(value: Double): AmountTone = when {
    value > 0 -> AmountTone.Positive
    value < 0 -> AmountTone.Negative
    else -> AmountTone.Neutral
}

@Preview(showBackground = true)
@Composable
private fun TransactionListItemPreview() {
    SuacTheme {
        Column {
            TransactionListItem(
                title = "Nov 21, 2026",
                subtitle = "Kutlíkova · Martin Styk",
                amount = "−4,12 €",
                amountTone = AmountTone.Negative,
                detail = "13.42 kWh",
                leadingIcon = SuacIcons.Power,
                badgeIcon = SuacIcons.Rfid,
                onOpenStats = {},
            )
            TransactionListItem(
                title = "Nov 20, 2026",
                subtitle = "Credit",
                amount = "+50,00 €",
                amountTone = AmountTone.Positive,
                leadingIcon = SuacIcons.TopUp,
            )
        }
    }
}
