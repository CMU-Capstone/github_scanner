package com.scanner.model;

public class Repo {
	String name;
	String type;
	String industry;
	String technology;
	String data;
	
	public Repo(String name, String type, String industry, String technology, String data) {
		super();
		this.name = name;
		this.type = type;
		this.industry = industry;
		this.technology = technology;
		this.data = data;
	}

	@Override
	public String toString() {
		return "Repo [name=" + name + ", type=" + type + ", industry=" + industry + ", technology=" + technology
				+ ", data=" + data + "]";
	}
	
	@Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + industry.hashCode();
        result = 31 * result + technology.hashCode();
        return result;
    }
	
	@Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Repo)) {
            return false;
        }

        Repo repo = (Repo) o;

        return repo.data.equals(data);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
	
	
}
