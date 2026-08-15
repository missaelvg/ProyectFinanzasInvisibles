# Finanzas Invisibles

Aplicación académica de finanzas personales que detecta posibles cargos desde notificaciones de Android, los clasifica y pide confirmación antes de incluirlos en los totales.

## Alcance actual

La entrega evaluable es la aplicación Android. Comparte modelos, reglas de clasificación y UI con Kotlin Multiplatform; iOS y Web conservan implementaciones locales de demostración, sin Firebase Auth ni lectura pasiva de notificaciones.

No se utiliza autenticación biométrica. El acceso se realiza con correo y contraseña mediante Firebase Authentication.

## Flujo principal

1. `NotificationReaderService` recibe una notificación autorizada por el usuario.
2. `ExpenseParser` descarta ingresos/reembolsos y extrae un monto válido.
3. `GeminiHelper` combina la sugerencia remota con reglas locales de respaldo.
4. El gasto se guarda primero en `GastoDatabase` con estado `Pendiente`.
5. `GastoRepository` usa el mismo ID local como ID de Firestore.
6. Si no hay red, `SyncGastosWorker` reintenta los registros pendientes.
7. El usuario acepta, edita o descarta el movimiento desde Historial.

## Funciones alineadas con la Actividad Documental 11

- Registro e inicio de sesión con Firebase Auth.
- Perfil extendido, cambio seguro de correo/contraseña y racha por día calendario.
- Solicitud explícita de ubicación para sugerir la ciudad durante el registro.
- Acceso guiado a la autorización de lectura de notificaciones.
- Clasificación híbrida: backend Ktor/Railway y reglas regex locales.
- Dashboard semanal, distribución por categorías, historial editable y alertas.
- Metas con progreso, aportaciones manuales reales y recomendación con fallback.
- Sincronización con Firestore y WorkManager.
- Interfaz ES/EN (algunas pantallas operativas de Android permanecen en español).

## Configuración

1. Abre el proyecto con Android Studio compatible con JDK 17.
2. Verifica que `androidApp/google-services.json` corresponda a tu proyecto Firebase.
3. Publica las reglas de seguridad antes de probar con datos reales:

   ```bash
   firebase deploy --only firestore:rules
   ```

4. Compila e instala:

   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

5. En la app, entra a **Ajustes → Configurar permisos** y habilita el lector de notificaciones. La ubicación es opcional; la ciudad también puede escribirse manualmente.

## Pruebas

```bash
./gradlew :shared:testAndroidHostTest
```

Las pruebas cubren extracción de montos, separadores de miles/decimales, exclusión de ingresos y clasificación local. GitHub Actions ejecuta las pruebas y compila el APK en cada pull request.

## Privacidad

La app no solicita credenciales bancarias. El permiso de notificaciones permite leer contenido potencialmente sensible; debe activarse conscientemente y solo usarse con datos de prueba durante la evaluación.
