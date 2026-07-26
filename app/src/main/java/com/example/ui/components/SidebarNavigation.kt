package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekPrimary
import com.example.ui.viewmodel.ScreenTab

@Composable
fun NavigationDrawerContent(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color(0xFF001D36))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        // App Header Branding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleekPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Plateau Health Link",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "State Health Monitor",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFFD1E4FF)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color(0xFF003258)
        )

        Text(
            text = "STATE NETWORK & PHC MODULES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = Color(0xFF9ECAFF),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )

        val distinctTabs = listOf(
            ScreenTab.DASHBOARD to (Icons.Default.Dashboard to "System overview"),
            ScreenTab.FACILITIES_MAP to (Icons.Default.Map to "Geographic view"),
            ScreenTab.FACILITIES_LIST to (Icons.Default.Domain to "PHC, cottage & tertiary"),
            ScreenTab.MEDICAL_STAFF to (Icons.Default.MedicalServices to "Doctors & nurses"),
            ScreenTab.DRUG_INVENTORY to (Icons.Default.Medication to "Stock management"),
            ScreenTab.INVENTORY_ALERTS to (Icons.Default.NotificationsActive to "Low stock alerts & restock"),
            ScreenTab.DRUG_USAGE_SEASON to (Icons.Default.Timeline to "By season & illness"),

            ScreenTab.FUMIGATION to (Icons.Default.PestControl to "Schedule & vector control"),
            ScreenTab.PATIENTS to (Icons.Default.AssignmentInd to "Visits & family cards"),
            ScreenTab.BIRTH_RECORDS to (Icons.Default.ChildCare to "Birth rates & boy/girl ratio"),
            ScreenTab.STAFF_FEEDBACK to (Icons.Default.Build to "Facility issues & maintenance"),
            ScreenTab.SURVEILLANCE to (Icons.Default.Shield to "Outbreak detection & alerts"),
            ScreenTab.AI_FORECAST to (Icons.Default.AutoAwesome to "Seasonal drug needs"),
            ScreenTab.OUTBREAK_PREDICTION to (Icons.Default.ShowChart to "Anticipated Spikes vs 3-Yr Data"),
            ScreenTab.REPORTS to (Icons.Default.BarChart to "Analytics & trends"),
            ScreenTab.INSTALL_APP to (Icons.Default.GetApp to "Web PWA & APK install")
        )

        distinctTabs.forEach { (tab, details) ->
            val isSelected = currentTab == tab
            val (icon, subtitle) = details

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTabSelected(tab) },
                color = if (isSelected) SleekPrimary else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else Color(0xFFD1E4FF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            ),
                            color = if (isSelected) Color.White else Color(0xFFE2E2E6)
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = if (isSelected) Color(0xFFD1E4FF) else Color(0xFF9ECAFF).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // Developer Profile Tag
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF002B49)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SleekPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ST",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Sisi Technology Ltd",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Jos, Plateau State",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFFD1E4FF)
                        )
                    }
                }
            }
        }
    }
}

