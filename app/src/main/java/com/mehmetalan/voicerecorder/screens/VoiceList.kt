package com.mehmetalan.voicerecorder.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mehmetalan.voicerecorder.AudioRecorder
import com.mehmetalan.voicerecorder.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VoiceList(
    navController: NavHostController
) {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var recordedFiles by remember { mutableStateOf(audioRecorder.getRecordedFiles()) }
    var fileToDelete by remember { mutableStateOf<File?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.voice_list)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(recordedFiles.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.empty_list),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(recordedFiles) { file ->
                        Card {
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (isPlaying) {
                                                audioRecorder.stopPlaying()
                                                isPlaying = false
                                            } else {
                                                audioRecorder.startPlaying(file)
                                                isPlaying = true
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                            contentDescription = "Play Button"
                                        )
                                    }
                                    Text(
                                        text = file.name
                                    )
                                }
                                IconButton(
                                    onClick = {
                                              fileToDelete = file
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteOutline,
                                        contentDescription = "Delete Button"
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

            }
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
                confirmButton = {
                    Button(
                        onClick = {
                            file.delete()
                            recordedFiles = recordedFiles.filter { it != file }
                            fileToDelete = null
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.delete)
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            fileToDelete = null
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel)
                        )
                    }
                }
            )
        }
    }
}