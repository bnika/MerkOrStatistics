package is.merkor.statistics.hibernate.data;

public interface PersistentObject {
	
	    public Long getId();
	    public void setId(Long id);

	    public Integer getVersion();
	    public void setVersion(Integer version);
	    
	    public boolean equals (Object o);
	    public int hashCode();
}

