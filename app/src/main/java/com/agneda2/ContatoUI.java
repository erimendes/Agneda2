package com.agneda2;

import com.agneda2.POJO.ContatoVO;
import com.agneda2.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ContatoUI extends Activity {
    private static final int INCLUIR = 0;
    //private static final int ALTERAR = 1;
    ContatoVO lContatoVO;
    EditText txtNome;
    EditText txtEndereco;
    EditText txtTelefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contato);

        try
        {


            final Bundle data = (Bundle) getIntent().getExtras();
            int lint = data.getInt("tipo");
            if (lint == INCLUIR)
            {
                //quando for incluir um contato criamos uma nova instância
                lContatoVO = new ContatoVO();
            }else{
                //quando for alterar o contato carregamos a classe que veio por Bundle
                lContatoVO = (ContatoVO)data.getSerializable("agenda");
            }

            //Criação dos objetos da Activity
            txtNome = (EditText)findViewById(R.id.edtNome);
            txtEndereco = (EditText)findViewById(R.id.edtEndereco);
            txtTelefone = (EditText)findViewById(R.id.edtTelefone);

            //Carregando os objetos com os dados do Contato
            //caso seja uma inclusão ele virá carregado com os atributos text
            //definido no arquivo main.xml
            txtNome.setText(lContatoVO.getNome());
            txtEndereco.setText(lContatoVO.getEndereco());
            txtTelefone.setText(lContatoVO.getTelefone());
        }catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
    }

    public void btnConfirmar_click(View view)
    {
        try
        {
            //Quando confirmar a inclusão ou alteração deve-se devolver
            //o registro com os dados preenchidos na tela e informar
            //o RESULT_OK e em seguida finalizar a Activity


            Intent data = new Intent();
            lContatoVO.setNome(txtNome.getText().toString());
            lContatoVO.setEndereco(txtEndereco.getText().toString());
            lContatoVO.setTelefone(txtTelefone.getText().toString());
            data.putExtra("agenda", lContatoVO);
            setResult(Activity.RESULT_OK, data);
            finish();
        }catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
    }

    public void btnCancelar_click(View view)
    {
        try
        {
            //Quando for simplesmente cancelar a operação de inclusão
            //ou alteração deve-se apenas informar o RESULT_CANCELED
            //e em seguida finalizar a Activity

            setResult(Activity.RESULT_CANCELED);
            finish();
        }catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
    }

    public void toast (String msg)
    {
        Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }

    private void trace (String msg)
    {
        toast (msg);
    }

}