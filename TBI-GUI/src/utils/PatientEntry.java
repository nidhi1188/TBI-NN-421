package utils;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;

public class PatientEntry extends InfoEntry implements Comparable, Serializable{
	
	public PatientEntry(String n, String u, Key k) {
		super(n, u, k);
	}

	public int compareTo(Object o) {
		return super.compareTo(o);
	}
	
}
