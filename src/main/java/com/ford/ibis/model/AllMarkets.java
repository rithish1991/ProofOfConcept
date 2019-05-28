package com.ford.ibis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllMarkets {
	
	private String value;	
	private String label;
	
	
	@Override
    public boolean equals(Object other) {
        return (other != null && getClass() == other.getClass() && value != null)
            ? value.equals(((AllMarkets) other).value)
            : (other == this);
    }

    @Override
    public int hashCode() {
        return (value != null) 
            ? (getClass().hashCode() + value.hashCode())
            : super.hashCode();
    }
  
}
