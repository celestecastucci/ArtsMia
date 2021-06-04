package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	/**
	 *  HO MODIFICATO PER AGGIUNGERE L'ID MAP E CREARLA !!!
	 * @param map
	 */
	//era List<ArtObject>
	//cambiamo in void con idMap
	public void  listObjects(Map<Integer,ArtObject> map) {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!map.containsKey(res.getInt("object_id"))) {
				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				map.put(artObj.getId(), artObj);
				}
				
			
			}
			conn.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
/**
 * PER CALCOLARE IL PESO DI UN ARCO CREO IL METODO
 * SICCOME IL PESO E' QUANTE VOLTE DUE OGGETTI SONO STATI ESPOSTI INSIEME
 * CONTO OGNI VOLTA LA RIGA CHE DUE OGGETTI SONO DELLA STESSA EXIBITION 
 * la riga l'ho definita con una SELECT COUNT(*) as peso 
 * dichiaro variabile int peso=0; che sarà data dal res.getInt("peso") della query
 * @param a1
 * @param a2
 * @return
 */
	public int getPeso(ArtObject a1, ArtObject a2) {
	
		String sql=" SELECT count(*) as peso "
		    + "FROM exhibition_objects e1, exhibition_objects e2 "
		    + "WHERE e1.exhibition_id= e2.exhibition_id AND e1.object_id= ? AND   e2.object_id= ? ";
		
		Connection conn= DBConnect.getConnection();
		try {
			
			PreparedStatement st= conn.prepareStatement(sql);
			st.setInt(1, a1.getId());
			st.setInt(2, a2.getId());
			ResultSet res= st.executeQuery();
			int peso=0;
			if(res.next()) {
				peso= res.getInt("peso");
			}
			st.close();
			res.close();
			conn.close();
			return peso;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;  //perche il metodo è un int, se non ho un peso vuol dire che non sono stati esposti insieme
		}
		
		//CREO CLASSE ADIACENZA NEL PACKAGE MODEL
		//CHE MAPPA I DATI CHE ESTRAIAMO DALLA QUERY 
	}
	
	/**
	 * METODO CHE RITORNA UNA LISTA DELLE ADIACENZE 
	 * CREO UN OGGETTO ADIACENZA CHE CONTIENE LA TRIPLA : ID1, ID2, PESO
	 * AGGIUNGO QUESTO OGGETTO ALLA LISTA FINALE (listaAdiacenze)
	 * @return lista di adiacenze
	 */
	public List<Adiacenza> getAdiacenze() {
		String sql=" SELECT e1.object_id AS id1, e2.object_id AS id2, COUNT(*) AS peso "
				+ "FROM exhibition_objects e1, exhibition_objects e2 "
				+ "WHERE e1.exhibition_id =e2.exhibition_id AND e1.object_id > e2.object_id "
				+ "GROUP BY e1.object_id, e2.object_id";
	
		
		Connection conn= DBConnect.getConnection();
		List<Adiacenza>listaAdiacenze= new ArrayList<>();
		
		try {
			
			PreparedStatement st= conn.prepareStatement(sql);
			
			ResultSet res= st.executeQuery();
			while(res.next()) {
				listaAdiacenze.add(new Adiacenza(res.getInt("id1"), res.getInt("id2"), res.getInt("peso")));
				
			}
			st.close();
			res.close();
			conn.close();
			return listaAdiacenze;
		} catch (SQLException e) {
			e.printStackTrace();
			return null; 
		}
	
	
	}
	
	
}
