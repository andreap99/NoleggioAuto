package polito.it.noleggio.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.PriorityQueue;

import polito.it.noleggio.model.Event.EventType;

public class Simulator {
	
	// CODA DEGLI EVENTI
	private PriorityQueue<Event> queue = new PriorityQueue<>();
	
	//PARAMETRI DI SIMULAZIONE
	private int NC; //numero di auto
	private Duration T_IN; // intervallo tra clienti
	
	private final LocalTime oraApertura = LocalTime.of(8, 00);
	private final LocalTime oraChiusura = LocalTime.of(17, 00);
	
	// MODELLO DEL MONDO
	private int nAuto; // auto disponibili nel deposito
	
	// VALORI DA CALCOLARE
	private int clienti;
	private int insoddisfatti;
	
	// METODI PER IMPOSTARE I PARAMETRI
	public void setNumCars(int i) {
		this.NC = i;
	}

	public void setClientFrequency(Duration of) {
		this.T_IN = of;
	}

	public int getClienti() {
		return clienti;
	}

	public int getInsoddisfatti() {
		return insoddisfatti;
	}

	// SIMULAZIONE VERA E PROPRIA
	
	public void run() {
		// preparazione iniziale (mondo + coda degi eventi)
		this.nAuto = NC;
		this.clienti = 0;
		this.insoddisfatti = 0;
		
		this.queue.clear();
		LocalTime oraArrivoCliente = this.oraApertura;
		do {
			
			Event e = new Event(oraArrivoCliente, EventType.NEW_CLIENT);
			this.queue.add(e);
			oraArrivoCliente = oraArrivoCliente.plus(T_IN);
			
		}while ( oraArrivoCliente.isBefore(oraChiusura));
		
		// esecuzione del ciclo di simulazione
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e.toString());
			processEvent(e);
			}
		}
	
	private void processEvent(Event e) {
		switch(e.getType()) {
		case NEW_CLIENT:
			
			if(this.nAuto>0) {
				// il cliente viene servito, autp noleggiata
				//1) aggiorna modello del mondo
				this.nAuto--;
				//2) aggiorna i risultati
				this.clienti++;
				//3) genera nuovi eventi
				double num = Math.random(); //[0,1)
				Duration travel;
				if(num<1.0/3.0)
					travel = Duration.of(1, ChronoUnit.HOURS);
				else if(num<2.0/3.0)
					travel = Duration.of(2,  ChronoUnit.HOURS);
				else
					travel = Duration.of(3, ChronoUnit.HOURS);
				
				Event nuovo = new Event(e.getTime().plus(travel), EventType.CAR_RETURNED);
				this.queue.add(nuovo);
				
			}else {
				//cliente insoddisfatto
				this.clienti++;
				this.insoddisfatti++;
			}
			
			break;
		
		case CAR_RETURNED:
			
			this.nAuto++;
			
			break;
		}
	}
}
