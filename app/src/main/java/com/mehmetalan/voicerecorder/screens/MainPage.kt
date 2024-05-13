package com.mehmetalan.voicerecorder.screens

import android.Manifest

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController

@Composable
fun AudioRecorderApp(
    navController: NavHostController
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordedFiles by remember { mutableStateOf(emptyList<File>()) }
    var showDialog by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("") }
    var fileToDelete by remember { mutableStateOf<File?>(null) }

    fun requestPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission && activity != null) {
            activity.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    LaunchedEffect(Unit) {
        recordedFiles = audioRecorder.getRecordedFiles()
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
            Text(
                text = stringResource(id = R.string.recorder)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.list),
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
                    }
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.file_name)
                    )
                },
                text = {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { if(it.length<30) {
                            fileName = it
                        }
                        },
                        label = { 
                            Text(
                                text = stringResource(id = R.string.enter_file_name)
                            ) 
                                },
                        singleLine = true,
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        if (fileName.isNotBlank()) {
                            audioRecorder.stopRecording()
                            val newFileName = "$fileName.mp3"
                            val audioDir = context.getExternalFilesDir(null)
                            val oldFile = audioRecorder.getRecordedFiles().lastOrNull()
                            if (oldFile != null) {
                                val newFile = File(audioDir, newFileName)
                                oldFile.renameTo(newFile)
                                recordedFiles = recordedFiles + newFile
                            }
                        }
                        isRecording = false
                    }) {
                        Text(
                            text = stringResource(id = R.string.okay)
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                        isRecording = false
                    }) {
                        Text(
                            text = stringResource(id = R.string.cancel)
                        )
                    }
                }
            )
        }
        fileToDelete?.let { file ->
            AlertDialog(
                onDismissRequest = {
                    fileToDelete = null
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.delete_file)
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.delete_file_question)
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        file.delete()
                        recordedFiles = recordedFiles.filter { it != file }
                        fileToDelete = null
                    }) {
                        Text(
                            text = stringResource(id = R.string.delete)
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        fileToDelete = null
                    }) {
                        Text(
                            text = stringResource(id = R.string.cancel)
                        )
                    }
                }
            )
        }
    }
}