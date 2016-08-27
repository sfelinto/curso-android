package br.com.alura.agenda;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by shfelinto on 25/08/2016.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng posicaoDaEscola =
                pegaCordenadaDoEndereco("Rua Jornalista Mario Machado Amaral 87, Campo Grande, Rio de Janeiro");
        if (posicaoDaEscola != null){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicaoDaEscola, 17);
            googleMap.moveCamera(update);
        }
        AlunoDAO alunoDao = new AlunoDAO(getContext());
        for(Aluno aluno : alunoDao.buscaAlunos()){
            LatLng coordenadaDoAluno = pegaCordenadaDoEndereco(aluno.getEndereco());
            MarkerOptions marcador = new MarkerOptions();
            marcador.position(coordenadaDoAluno);
            marcador.title(aluno.getNome() + aluno.getEndereco());
            marcador.snippet(String.valueOf(aluno.getNota()));
            googleMap.addMarker(marcador);
        }
        alunoDao.close();

        new Localizador(getContext(), googleMap);
    }

    private LatLng pegaCordenadaDoEndereco(String endereco){
        try{
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> resultados =
                    geocoder.getFromLocationName(endereco, 1);
            if(!resultados.isEmpty()){
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(), resultados.get(0).getLongitude());
                return posicao;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}