package application;

public class Server {

	String qName;
	boolean qBusy;
	Integer qDeclined;
	Long qCurrentTask;
	Integer qCompletedTask;
	
	public Server(String Name) 
	{
		
		qName = "Uknown server";
		qBusy = false;
		qDeclined = 0;
		qCurrentTask = -1l;
		qCompletedTask=0;
		qName = Name;

	}
	
	/** ���� Input() ������ true, ������ ������ ����� � ��������� */
	public boolean Input(Long ProgramsComplexity)
	{
		if (!qBusy)
		{
			qCurrentTask = ProgramsComplexity;
			qBusy = true;
			return true;
		}
		else qDeclined++;
		
		return false;
	}
	
	/** ���� ������ �������. �� ���� ���� ������. */
	public void tick()
	{
		if (qCurrentTask>0) qCurrentTask--;
		else if (qCurrentTask==0) {qBusy=false; qCompletedTask++; qCurrentTask=-1L;}
		else {return;}
	}

}
