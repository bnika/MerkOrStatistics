package is.merkor.statistics.hibernate.data;

import org.apache.commons.lang.builder.HashCodeBuilder;

public abstract class AbstractPersistentObject implements PersistentObject {
	
	private Long id;
	private Integer version;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
	
    public int hashCode() {
    	return new HashCodeBuilder()
        	.append(getId())
            .toHashCode();
    }
}

