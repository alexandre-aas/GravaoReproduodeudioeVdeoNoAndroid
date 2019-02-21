package com.s.d.a.a.gravaoreproduodeudioevdeonoandroid;

import java.io.IOException;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Button;
import android.view.View;
import android.media.MediaPlayer;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.support.v4.app.ActivityCompat;


public class GravacaoReproducaoActivity extends AppCompatActivity {

    private static final int CODIGO_SOLICITACAO_GRAVACAO        = 201;
    private static final int CODIGO_SOLICITACAO_ARMAZENAMENTO   = 202;

    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;

    private static String localArquivoAudio;
    private static Button btnExecutar;
    private static Button btnParar;
    private static Button btnGravar;

    private boolean estaGravando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravacao_reproducao);

        btnGravar   = findViewById(R.id.btnGravar);
        btnExecutar = findViewById(R.id.btnExecutar);
        btnParar    = findViewById(R.id.btnParar);

        if (!temMicrofone())
        {
            btnParar.setEnabled(false);
            btnExecutar.setEnabled(false);
            btnGravar.setEnabled(false);
        } else {
            btnExecutar.setEnabled(false);
            btnParar.setEnabled(false);
        }

        localArquivoAudio = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/meu_audio.3gp";
        solicitarPermissao(Manifest.permission.RECORD_AUDIO,
                CODIGO_SOLICITACAO_GRAVACAO);
    }

    protected boolean temMicrofone() {
        PackageManager pManager = this.getPackageManager();
        return pManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void gravarAudioVideo (View view) throws IOException
    {
        estaGravando = true;
        btnParar.setEnabled(true);
        btnExecutar.setEnabled(false);
        btnGravar.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(localArquivoAudio);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    public void pararExecucaoAudioVideo (View view)
    {

        btnParar.setEnabled(false);
        btnExecutar.setEnabled(true);

        if (estaGravando)
        {
            btnGravar.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            estaGravando = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            btnGravar.setEnabled(true);
        }
    }

    public void executarAudioVideo (View view) throws IOException
    {
        btnExecutar.setEnabled(false);
        btnGravar.setEnabled(false);
        btnParar.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(localArquivoAudio);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    protected void solicitarPermissao(String tipoDePermissao, int codigoDaSolicitacao) {
        int permissaao = ContextCompat.checkSelfPermission(this, tipoDePermissao);

        if (permissaao != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{tipoDePermissao}, codigoDaSolicitacao
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int codigoDoPedido, String listaDePermissoes[], int[] listaDeConcessoes) {
        switch (codigoDoPedido) {
            case CODIGO_SOLICITACAO_GRAVACAO: {

                if (listaDeConcessoes.length == 0
                        || listaDeConcessoes[0] != PackageManager.PERMISSION_GRANTED) {

                    btnGravar.setEnabled(false);

                    Toast.makeText(this, "Necessário permissão de gravação!",
                            Toast.LENGTH_LONG).show();
                } else {
                    solicitarPermissao(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            CODIGO_SOLICITACAO_ARMAZENAMENTO);
                }
                return;
            }
            case CODIGO_SOLICITACAO_ARMAZENAMENTO: {

                if (listaDeConcessoes.length == 0
                        || listaDeConcessoes[0] != PackageManager.PERMISSION_GRANTED) {
                    btnGravar.setEnabled(false);
                    Toast.makeText(this,
                            "Necessário permissão de armazenamento externo!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
