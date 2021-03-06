package br.com.onpressure.projeto.onpressure.Activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.onpressure.projeto.onpressure.Fragmentos.Ajuda_Frag;
import br.com.onpressure.projeto.onpressure.Model.PressaoArterial.PressaoArterialDAO;
import br.com.onpressure.projeto.onpressure.R;

public class PressaoArterialActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText txtPressaoDiastolica;
    private EditText txtPressaoSistolica;
    private EditText txtFrequenciaCardiaca;
    private TextView txtInfoPressao;
    private Button btn_ajuda;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressao_arterial);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        txtFrequenciaCardiaca = findViewById(R.id.txtFrequenciaCardiaca);
        txtPressaoDiastolica = findViewById(R.id.txtPressaoDiastolica);
        txtPressaoSistolica = findViewById(R.id.txtPressaoSistolica);
        btn_ajuda = findViewById(R.id.btn_ajuda);

        btnRegistrar.setOnClickListener(this);

        btn_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getFragmentManager();
                Ajuda_Frag dialogFragment = new Ajuda_Frag();
                dialogFragment.show(fm, "Sample Fragment");

            }
        });

    }

    @Override
    public void onClick(View v) {

        if (validarCampos() == true) {
            double pas = Double.parseDouble(txtPressaoSistolica.getText().toString().replace(",", "."));
            double pad = Double.parseDouble(txtPressaoDiastolica.getText().toString().replace(",", "."));
            classificarPressaoArterial(pas, pad);
        }else{
            Snackbar.make(v, "Verifique os campos obrigatórios", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    public void classificarPressaoArterial(double pas, double pad) {

        String informacao = "-";
        String dica = "Continue monitorando sua pressão arterial.";

        if (pas < 120 && pad < 80) {
            informacao = "Sua classificação de risco é: Normal.";
            dica = "Verifique a pressão arterial mensalmente.";
        }
        if ((pas >= 120 && pas <= 129) && (pad < 80)) {
            informacao = "Sua classificação de risco é: Elevada.";
            dica = "Evite o excesso de peso.";
        }
        if ((pas >= 130 && pas <= 139) || (pad >= 80 && pad <= 89)) {
            informacao = "Sua classificação de risco é: Hipertensão Estágio 1.";
            dica = "Mantenha uma alimentação saudável.";
        }
        if ((pas >= 140) || (pad >= 90)) {
            informacao = "Sua classificação de risco é: Hipertensão Estágio 2.";
            dica = "Reduza o consumo de bebidas alcoólicas.";
        }
        if ((pas > 180) || (pad > 120)) {
            informacao = "Sua classificação de risco é: Urgência hipertensiva!";
            dica = " Verifique novamente em farmácia ou ambulatório, caso persista há necessidade de atendimento médico!";
        }

        salvarPressaoArterial(informacao, dica);
    }

    public boolean validarCampos() {

        boolean valido = false;

        if (TextUtils.isEmpty(txtFrequenciaCardiaca.getText())) {
            txtFrequenciaCardiaca.setError("Campo obrigatório");
            valido = false;
        } else if (TextUtils.isEmpty(txtPressaoDiastolica.getText())) {
            txtPressaoDiastolica.setError("Campo obrigatório");
            valido = false;
        } else if (TextUtils.isEmpty(txtPressaoSistolica.getText())) {
            txtPressaoSistolica.setError("Campo obrigatório");
            valido = false;
        } else {
            valido = true;
        }

        return valido;
    }

    public void salvarPressaoArterial(String informacao, String dica) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        float pressaoDiastolica = Float.parseFloat(txtPressaoDiastolica.getText().toString().replace(",", "."));
        float pressaoSistolica = Float.parseFloat(txtPressaoSistolica.getText().toString().replace(",", "."));
        float frequenciaCardiaca = Float.parseFloat(txtFrequenciaCardiaca.getText().toString().replace(",", "."));
        String infoPressao = informacao;
        String data = formatter.format(Calendar.getInstance().getTime());

        PressaoArterialDAO dao = new PressaoArterialDAO(getBaseContext());
        boolean sucesso = dao.salvar(pressaoDiastolica, pressaoSistolica, frequenciaCardiaca, infoPressao, data);

        if (sucesso) {

            txtFrequenciaCardiaca.setText("");
            txtPressaoSistolica.setText("");
            txtPressaoDiastolica.setText("");

            Toast.makeText(PressaoArterialActivity.this, "Dados Salvos com sucesso!", Toast.LENGTH_LONG).show();

            if (informacao.equals("-")) {
                AlertDialog alertDialog = new AlertDialog.Builder(PressaoArterialActivity.this).create();
                alertDialog.setTitle("Dados coletados com sucesso.");
                alertDialog.setMessage(dica);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(PressaoArterialActivity.this).create();
                alertDialog.setTitle("Dados coletados com sucesso.");
                alertDialog.setMessage(informacao);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}