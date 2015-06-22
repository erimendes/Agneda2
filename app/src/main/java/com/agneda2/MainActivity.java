package com.agneda2;

import android.support.v7.app.ActionBarActivity;

import java.util.List;

import com.agneda2.DAO.ContatoAdapter;
import com.agneda2.DAO.ContatoDAO;
import com.agneda2.POJO.ContatoVO;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ListActivity {

    private static final int INCLUIR = 0;
    private static final int ALTERAR = 1;

    private ContatoDAO lContaoDAO; //inst�ncia respons�vel pela persist�ncia dos dados
    List<ContatoVO> lstContatos;  //lista de contatos cadastrados no BD
    ContatoAdapter adapter;   //Adapter respons�vel por apresentar os contatos na tela

    boolean blnShort = false;
    int Posicao = 0;    //determinar a posi��o do contato dentro da lista lstContatos

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        lContaoDAO = new ContatoDAO(this);
        lContaoDAO.open();

        lstContatos = lContaoDAO.Consultar();

        adapter = new ContatoAdapter(this, lstContatos);
        setListAdapter(adapter);

        registerForContextMenu(getListView());
    }

    // Este evento ser� chamado pelo atributo onClick
    // que est� definido no bot�o criado no arquivo main.xml
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                InserirContato();
                break;
        }
    }

    //Rotina executada quando finalizar a Activity ContatoUI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContatoVO lAgendaVO = null;

        try
        {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK)
            {
                //obtem dados inseridos/alterados na Activity ContatoUI
                lAgendaVO = (ContatoVO)data.getExtras().getSerializable("agenda");

                //o valor do requestCode foi definido na fun��o startActivityForResult
                if (requestCode == INCLUIR)
                {
                    //verifica se digitou algo no nome do contato
                    if (!lAgendaVO.getNome().equals(""))
                    {
                        //necess�rio abrir novamente o BD pois ele foi fechado no m�todo onPause()
                        lContaoDAO.open();

                        //insere o contato no Banco de Dados SQLite
                        lContaoDAO.Inserir(lAgendaVO);

                        //insere o contato na lista de contatos em mem�ria
                        lstContatos.add(lAgendaVO);
                    }
                }else if (requestCode == ALTERAR){
                    lContaoDAO.open();
                    //atualiza o contato no Banco de Dados SQLite
                    lContaoDAO.Alterar(lAgendaVO);

                    //atualiza o contato na lista de contatos em mem�ria
                    lstContatos.set(Posicao, lAgendaVO);
                }

                //m�todo respons�vel pela atualiza da lista de dados na tela
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
    }

    private void InserirContato(){
        try
        {
            //a vari�vel "tipo" tem a fun��o de definir o comportamento da Activity
            //ContatoUI, agora a vari�vel tipo est� definida com o valor "0" para
            //informar que ser� uma inclus�o de Contato

            Intent it = new Intent(this, ContatoUI.class);
            it.putExtra("tipo", INCLUIR);
            startActivityForResult(it, INCLUIR);//chama a tela e incus�o
        }
        catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        //quando a Activity main receber o foco novamente abre-se novamente a conex�o
        lContaoDAO.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //toda vez que o programa peder o foco fecha-se a conex�o com o BD
        lContaoDAO.close();
        super.onPause();
    }

    public void toast (String msg)
    {
        Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    }

    private void trace (String msg)
    {
        toast (msg);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        try
        {
            //Cria��o do popup menu com as op��es que termos sobre
            //nossos Contatos

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            if (!blnShort)
            {
                Posicao = info.position;
            }
            blnShort = false;

            menu.setHeaderTitle("Selecione:");
            //a origem dos dados do menu est� definido no arquivo arrays.xml
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
    }


    //Este m�todo � disparado quando o usu�rio clicar em um item do ContextMenu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContatoVO lAgendaVO = null;
        try
        {
            int menuItemIndex = item.getItemId();

            //Carregar a inst�ncia POJO com a posi��o selecionada na tela
            lAgendaVO = (ContatoVO) getListAdapter().getItem(Posicao);

            if (menuItemIndex == 0){
                //Carregar a Activity ContatoUI com o registro selecionado na tela

                Intent it = new Intent(this, ContatoUI.class);
                it.putExtra("tipo", ALTERAR);
                it.putExtra("agenda", lAgendaVO);
                startActivityForResult(it, ALTERAR); //chama a tela de altera��o
            }else if (menuItemIndex == 1){
                //Excluir do Banco de Dados e da tela o registro selecionado

                lContaoDAO.Excluir(lAgendaVO);
                lstContatos.remove(lAgendaVO);
                adapter.notifyDataSetChanged(); //atualiza a tela
            }
        }catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
        return true;

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //por padr�o o ContextMenu, s� � executado atrav�s de LongClick, mas
        //nesse caso toda vez que executar um ShortClick, abriremos o menu
        //e tamb�m guardaremos qual a posi��o do itm selecionado

        Posicao = position;
        blnShort = true;
        this.openContextMenu(l);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


}
