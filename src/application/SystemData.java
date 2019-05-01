package application;

public class SystemData 
{
	
	private String PhysicalData;
	private String AnalyticalData;
	private String CalculatedData;
	
	public SystemData() {
		PhysicalData = "empty_string";
		AnalyticalData = "empty_string";
		CalculatedData = "empty_string";
	}
	
	public SystemData(String ph, String an, String ca) {
		PhysicalData = ph;
		AnalyticalData = an;
		CalculatedData = ca;
	}

	public String getPhysicalData() {
		return PhysicalData;
	}

	public String getAnalyticalData() {
		return AnalyticalData;
	}

	public String getCalculatedData() {
		return CalculatedData;
	}

	public void setPhysicalData(String physicalData) {
		PhysicalData = physicalData;
	}

	public void setAnalyticalData(String analyticalData) {
		AnalyticalData = analyticalData;
	}

	public void setCalculatedData(String calculatedData) {
		CalculatedData = calculatedData;
	}

}
