package politica.br;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;


import politica.br.Client.ApiClient;
import politica.br.Models.Candidato;
import politica.br.Service.ApiServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalheActivity extends AppCompatActivity {

    private TextView nome;
    private Candidato candidato;
    private ImageView foto;
    private ApiServices apiServices;
    private ProgressDialog progress = null;
    private Long idCandidato;
    private TextView partido;
    private TextView propostas;
    private TextView site;
    private TextView detalhes;
    private TextView totalVotos;
    private Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        nome = findViewById(R.id.nome);
        foto = findViewById(R.id.foto);
        partido = findViewById(R.id.partido);
        propostas = findViewById(R.id.propostas);
        site = findViewById(R.id.site);
        detalhes = findViewById(R.id.detalhes);
        totalVotos = findViewById(R.id.totalVotos);

        enviar = findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(idCandidato != null){

                    votarCandidato(idCandidato);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent != null){
            idCandidato = intent.getLongExtra("id", 1l);
            if(idCandidato != null){
                getCandidato(idCandidato);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void getCandidato(Long id) {
        progress = ProgressDialog.show(DetalheActivity.this,
                "Aguarde ...", "Recebendo informações da web", true, true);
        apiServices = ApiClient.getClient().create(ApiServices.class);

        Call<Candidato> call = apiServices.getCandidato ("application/json", id);
        call.enqueue(new Callback<Candidato>() {
            @Override
            public void onResponse(Call<Candidato> call, Response<Candidato> response) {
                if (response.isSuccessful()) {
                    candidato =  response.body();

                    if(candidato != null){
                        nome.setText(candidato.getNome());
                        partido.setText(candidato.getPartido());
                        propostas.setText(candidato.getPropostas());
                        site.setText(candidato.getSite());
                        totalVotos.setText(candidato.getTotalVotos().toString());
                        detalhes.setText(candidato.getDetalhes());
                        Ion.with(foto)
                                .centerCrop()
                                .placeholder(R.drawable.place_holder)
                                .error(R.drawable.error)
                                .animateIn(R.anim.fade_in)
                                .load(Constants.PATH_URL + "/" + candidato.getFoto());
                    }
                    progress.dismiss();

                } else {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Candidato> call, Throwable t) {
                progress.dismiss();
                t.printStackTrace();
            }
        });
    }

    private void votarCandidato(Long id) {
        progress = ProgressDialog.show(DetalheActivity.this,
                "Aguarde ...", "Recebendo informações da web", true, true);
        apiServices = ApiClient.getClient().create(ApiServices.class);

        Call<Candidato> call = apiServices.sendVoto ("application/json", id);
        call.enqueue(new Callback<Candidato>() {
            @Override
            public void onResponse(Call<Candidato> call, Response<Candidato> response) {
                if (response.isSuccessful()) {
                    candidato =  response.body();
                    Toast.makeText(getApplicationContext(),"Voto computado com sucesso!", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(),"Deu ruim!", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                    finish();

                }
            }

            @Override
            public void onFailure(Call<Candidato> call, Throwable t) {
                progress.dismiss();
                t.printStackTrace();
            }
        });
    }

}
