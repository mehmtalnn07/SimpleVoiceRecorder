package com.mehmetalan.voicerecorder.screens

import android.Manifest

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mehmetalan.voicerecorder.AudioRecorder
import com.mehmetalan.voicerecorder.R
import java.io.File
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import kotlin.math.sin

@Composable
fun AudioRecorderApp(
    navController: NavHostController
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity // ComponentActivity'ye erişim

    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordedFiles by remember { mutableStateOf(emptyList<File>()) }
    var showDialog by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("") }
    var fileToDelete by remember { mutableStateOf<File?>(null) }

    // İzin kontrolü ve gerekirse izin istemek için yardımcı fonksiyon
    fun requestPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission && activity != null) {
            // İzin isteme için standart Activity API'sini kullanarak izin isteyin
            activity.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    LaunchedEffect(Unit) {
        recordedFiles = audioRecorder.getRecordedFiles() // Kaydedilen dosyaları yükle
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Ses kaydedici")

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Liste",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(route = "voiceList")
                        }
                )
                IconButton(onClick = {
                }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 200.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(id = if (isRecording) R.drawable.p else R.drawable.record),
                contentDescription = null,
                modifier = Modifier
                    .size(75.dp)
                    .clickable {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!hasPermission) {
                            requestPermission()
                        } else {
                            if (isRecording) {
                                showDialog = true
                            } else {
                                audioRecorder.startRecording("Geçici_Kayıt")
                            }
                            isRecording = !isRecording
                        }
                        /*if (!isRecording) {
                            requestPermission() // İzin kontrolü ve isteme
                            audioRecorder.startRecording("Geçici_Kayıt")
                        } else {
                            showDialog = true // Dosya adı diyalogu
                        }
                        isRecording = !isRecording*/
                    }
            )
        }

        // Dosya adı için diyalog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text("Dosya Adı")
                },
                text = {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { if(it.length<30) {
                            fileName = it
                        }
                        },
                        label = { Text("Dosya Adı Girin") },
                        singleLine = true,
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        if (fileName.isNotBlank()) {
                            audioRecorder.stopRecording() // Kaydı durdur
                            val newFileName = "$fileName.mp3"
                            val audioDir = context.getExternalFilesDir(null)
                            val oldFile = audioRecorder.getRecordedFiles().lastOrNull() // Son kaydı alın
                            if (oldFile != null) {
                                val newFile = File(audioDir, newFileName)
                                oldFile.renameTo(newFile) // Dosyayı yeniden adlandır
                                recordedFiles = recordedFiles + newFile
                            }
                        }
                        isRecording = false
                    }) {
                        Text("Tamam")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                        isRecording = false
                    }) {
                        Text("İptal")
                    }
                }
            )
        }

        // Dosya silme için diyalog
        fileToDelete?.let { file ->
            AlertDialog(
                onDismissRequest = {
                    fileToDelete = null
                },
                title = {
                    Text("Dosyayı Sil")
                },
                text = {
                    Text("Bu dosyayı silmek istediğinizden emin misiniz?")
                },
                confirmButton = {
                    Button(onClick = {
                        file.delete()
                        recordedFiles = recordedFiles.filter { it != file }
                        fileToDelete = null
                    }) {
                        Text("Sil")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        fileToDelete = null
                    }) {
                        Text("İptal")
                    }
                }
            )
        }
    }
}