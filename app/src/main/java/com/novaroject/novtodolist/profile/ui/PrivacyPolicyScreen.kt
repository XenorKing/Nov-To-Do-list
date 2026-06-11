package com.novaroject.novtodolist.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple

// Fix #5 — Политика конфиденциальности
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // ─── Top bar ───
        Box(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0818))
                .statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF8888AA))
            }
            Text("Политика конфиденциальности", modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0E0C1C)).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Security, null, Modifier.size(36.dp), tint = NeonCyan)
                    Spacer(Modifier.height(8.dp))
                    Text("novTo-Do List", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Политика конфиденциальности", fontSize = 13.sp, color = Color(0xFF8888AA))
                    Spacer(Modifier.height(4.dp))
                    Text("Редакция от 11 июня 2025 г.", fontSize = 11.sp, color = Color(0xFF5555AA))
                }
            }

            PrivacySection(title = "1. Общие положения", neonColor = NeonCyan) {
                PrivacyText(
                    "Настоящая Политика конфиденциальности регулирует обработку персональных данных " +
                    "пользователей приложения «novTo-Do List», разработанного Nova Project (далее — «Приложение»).\n\n" +
                    "Используя Приложение, вы соглашаетесь с условиями настоящей Политики. " +
                    "Если вы не согласны с какими-либо условиями, пожалуйста, прекратите использование Приложения."
                )
            }

            PrivacySection(title = "2. Собираемые данные", neonColor = NeonPurple) {
                PrivacyText("Приложение собирает следующие данные:")
                PrivacyBullet("Адрес электронной почты и никнейм при регистрации")
                PrivacyBullet("Данные задач: название, описание, дата, приоритет, категория")
                PrivacyBullet("FCM-токен для отправки push-уведомлений")
                PrivacyBullet("Технические данные: идентификатор устройства, версия ОС")
                PrivacyText("\nМы НЕ собираем: геолокацию, контакты, медиафайлы или платёжные данные.")
            }

            PrivacySection(title = "3. Использование данных", neonColor = NeonCyan) {
                PrivacyText("Собранные данные используются исключительно для:")
                PrivacyBullet("Предоставления функционала Приложения (хранение задач, напоминания)")
                PrivacyBullet("Аутентификации и безопасности аккаунта")
                PrivacyBullet("Отправки push-уведомлений о задачах в указанное время")
                PrivacyBullet("Синхронизации данных между устройствами пользователя")
                PrivacyBullet("Улучшения качества Приложения")
            }

            PrivacySection(title = "4. Хранение данных", neonColor = NeonPurple) {
                PrivacyText(
                    "Данные хранятся в сервисах Google Firebase (Firestore и Authentication), " +
                    "расположенных на серверах Google LLC в соответствии со стандартами безопасности ISO 27001.\n\n" +
                    "Передача данных между устройством и серверами осуществляется по зашифрованному протоколу HTTPS/TLS.\n\n" +
                    "Данные хранятся до тех пор, пока существует ваш аккаунт. При удалении аккаунта все данные " +
                    "удаляются в течение 30 дней."
                )
            }

            PrivacySection(title = "5. Передача третьим лицам", neonColor = NeonCyan) {
                PrivacyText(
                    "Мы НЕ продаём и НЕ передаём ваши данные третьим лицам в коммерческих целях.\n\n" +
                    "Данные могут быть переданы только в следующих случаях:"
                )
                PrivacyBullet("По требованию уполномоченных органов в соответствии с законодательством РФ")
                PrivacyBullet("Техническим партнёрам (Google Firebase) в рамках предоставления инфраструктуры")
            }

            PrivacySection(title = "6. Уведомления", neonColor = NeonPurple) {
                PrivacyText(
                    "Приложение использует локальные уведомления (WorkManager) для напоминания о задачах.\n\n" +
                    "Вы можете в любой момент отключить уведомления в настройках Приложения или системных " +
                    "настройках Android."
                )
            }

            PrivacySection(title = "7. Права пользователя", neonColor = NeonCyan) {
                PrivacyText("Вы имеете право:")
                PrivacyBullet("Просматривать все свои данные в Приложении")
                PrivacyBullet("Изменять или удалять свои задачи в любое время")
                PrivacyBullet("Сменить никнейм в разделе «Профиль»")
                PrivacyBullet("Запросить удаление аккаунта, написав на novaprojecthelp@mail.ru")
                PrivacyBullet("Экспортировать свои данные по запросу")
            }

            PrivacySection(title = "8. Безопасность", neonColor = NeonPurple) {
                PrivacyText(
                    "Мы принимаем все разумные технические и организационные меры для защиты ваших данных:\n"
                )
                PrivacyBullet("Шифрование данных в транзите (TLS 1.3)")
                PrivacyBullet("Правила безопасности Firebase (Security Rules)")
                PrivacyBullet("Аутентификация на основе Firebase Auth")
                PrivacyBullet("Каждый пользователь видит только свои задачи")
            }

            PrivacySection(title = "9. Изменения политики", neonColor = NeonCyan) {
                PrivacyText(
                    "Мы оставляем за собой право изменять настоящую Политику. " +
                    "Об изменениях вы будете уведомлены через Приложение. " +
                    "Продолжение использования Приложения после изменений означает ваше согласие с новой редакцией."
                )
            }

            PrivacySection(title = "10. Контакты", neonColor = NeonPurple) {
                PrivacyText("По вопросам конфиденциальности обращайтесь:")
                PrivacyBullet("Email: novaprojecthelp@mail.ru")
                PrivacyBullet("Telegram: @NovaProjectNews")
                PrivacyBullet("Разработчик: Nova Project / XenorKing")
            }

            // Footer
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A0818)).padding(16.dp)
            ) {
                Text(
                    "© 2025 Nova Project. Все права защищены.\n" +
                    "Настоящая политика соответствует требованиям ФЗ-152 «О персональных данных».",
                    color = Color(0xFF4A4A7A), fontSize = 11.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PrivacySection(title: String, neonColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF100D20)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = neonColor, fontSize = 14.sp)
        HorizontalDivider(color = neonColor.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
        content()
    }
}

@Composable
private fun PrivacyText(text: String) {
    Text(text, color = Color(0xFFBBBBCC), fontSize = 13.sp, lineHeight = 20.sp)
}

@Composable
private fun PrivacyBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("• ", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text, color = Color(0xFFBBBBCC), fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.weight(1f))
    }
}
