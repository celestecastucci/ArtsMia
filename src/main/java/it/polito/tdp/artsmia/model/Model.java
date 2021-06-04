package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	//QUI INSERIAMO LA NOSTRA LOGICA APPLICATIVA
	
	//sappiamo il tipo di grafo: GRAFO PESATO SEMPLICE NON ORIENTATO :
	//sappiamo chi sono i vertici : tutti gli oggetti presenti --> 
	//sappiamo il tipo di archi: semplici con peso numerico --> DefaultWeightedEdge
	private Graph<ArtObject, DefaultWeightedEdge>grafo;
	
	private ArtsmiaDAO dao;
	
	//IDENTITY MAP: hashmap che salva al suo interno la corrispondenza tra id di un oggetto e l'oggetto
	private Map<Integer,ArtObject> idMap;
	//integer --> id di artObject è un int
	//CREO COSTRUTTORE CON SOLO IL DAO, NON METTO QUI IL GRAFO
	public Model() {
		dao= new ArtsmiaDAO();
		idMap= new HashMap<Integer,ArtObject>();
		
		
	}
	
	/**
	 * METODO IN CUI METTO LA NEW DEL GRAFO, COSI L'UTENTE OGNI VOLTA CHE CLICCA SUL BOTTONE PER CREARE IL GRAFO SONO SICURO CHE IL GRAFO VENGA DISTURTTO E RICREATO DA ZERO
	 * SE AVESSI MESSO IL NEW NEL COSTRUTTORE DEL MODEL AVREI DOVUTO OGNI VOLTA CANCELLARE IL GRAFO PER RIPULIRLO 
	 */
	public void creaGrafo() {
		grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	
	//AGGIUNGERE I VERTICI
		//1. recupero tutti gli ArtObject dal dao --> c'è gia una lista ma creo idMap
		//2. li inserisco come vertici
 //creo idMap e aggiungo nel dao
		dao.listObjects(idMap);
  //recupero tutti gli oggetti da idMap.values()
  Graphs.addAllVertices(grafo, idMap.values());
  
  //AGGIUNGERE GLI ARCHI 
  
    //APPROCCIO 1: FUNZIONA SOLO SE IL NUMERO DI VERTICI MOLTO BASSO come 30 vertici (non è questo il caso)
      //--> doppio ciclo for annidato per confrontare se i due vertici vanno collegati 
  
  /*for(ArtObject a1: this.grafo.vertexSet()) {
	  for(ArtObject a2: this.grafo.vertexSet()) {
		  //CONTROLLO CHE I DUE OGGETTI SIANO DIVERSI E CHE NON CI SIA GIA UN ARCO, VISTO CHE E' NON ORIENTATO SE C'E GIA NON SERVE RIMETTERE L'ARCO
		  if(!a1.equals(a2) && !this.grafo.containsEdge(a1, a2)) {
			  //se sono qua mi chiedo se devo collegare a1 ad a2
			  //posso avere un metodo nel dao che mi ritorni il peso dell'arco se c'è --> FACCIO ESEGUIRE CON QUERY
		      //se il peso è maggiore di zero allora posso aggiungere un arco tra i due vertici e gli setto quel peso
			  int peso= dao.getPeso(a1,a2);
		  if(peso>0)
			  Graphs.addEdge(this.grafo, a1, a2, peso);
		  }
	  }
  }
   */
  
  //APPROCCIO 2: evito il ciclo for doppio, nella query vado gia a vedere quanti sono collegati con e2 ad esempio
  //direttamente nella query verifico che e1 ed e2 siano diversi --> MA ANCORA UN PO LUNGO --> non ha senso usarlo, uso approccio 3
  
  //APPROCCIO 3: e' il piu veloce! svolgo tutto su heidi, non blocco piu niente
  
  //MI FACCIO DARE DAl database tutte le coppie, creo query ma dove la metto??? 
  //in questo caso non ho piu info semplici di tipo integer, ma sono info formate da ID1, ID2, PESO --> CREO CLASSE DI SUPPORTO "ADIACENZA" nel package Model
  //la classe adiacenza serve per estrarre le info tutte insieme:

  //QUI TORNA UTILE IDMAP: PERCHE VORREI UN OGGETTO MA HO MEMORIZZATO NELLA CLASSE ADIACENZA DEGLI INTEGER --> faccio la get che recuperi dalla mappa l'oggetto con quella id
  for(Adiacenza a: dao.getAdiacenze()) {
	  Graphs.addEdge(this.grafo, idMap.get(a.getId1()), idMap.get(a.getId2()), a.getPeso());
  }
	
	
	}
	

	

}
