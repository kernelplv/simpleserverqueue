package application;

import java.util.ArrayList;
import java.util.LinkedList;

public class QESystem {

	Integer qServersNumber; 		//Кол-во серверов
	Integer qBufferSize;	  		//Размер буфера (ед. на программу)
	boolean ExponentialMode;		//Режим экспоненциального распределения
	Double IntensionProgramStream; 	//Интенсивность поступления программ (в сек.)
	Double IntensionProgramSolving;	//Интенсивность обработки программы (в сек.)
	Double AverageTimeForSolving; 	//Среднее время обработки программы (в сек.)
	Double LoadFactor;				//Коэффициент загрузки системы
	boolean Undefined;				//Наличие неопределенности при n = фактору нагрузки
	Long UpTime;					//Время работы системы (в 0.001сек.)
	Long gInterval;					//Промежуток между генерацией программ
	Long gLastProgram;				//Последняя сгенерированная программа
	Integer DestroyedPrograms;		//Отклоненные программы
	ArrayList<Server> machines;		//Сервера обрабатывающие программы
	LinkedList<Long> buffer;		//Буфер системы
	ArrayList<oldProgram> Hystory;		//История сгенерированных программ для вычислений
	
	
	public QESystem(Integer ServersNumber, Integer BufferSize)
	{
		qServersNumber = 0;
		qBufferSize = 0;
		IntensionProgramStream = 1.5;
		IntensionProgramSolving = 0.5;
		AverageTimeForSolving = 2.0;
		LoadFactor = 0.0;
		Undefined = false;
		DestroyedPrograms = 0;
		UpTime = 0L;
		gInterval = 0L;
		gLastProgram = 0L;
		ExponentialMode = false;
		
		qServersNumber = ServersNumber;
		qBufferSize = BufferSize;
		
		machines = new ArrayList<Server>();
		buffer = new LinkedList<Long>();
		Hystory = new ArrayList<oldProgram>();
		
		for (int t=1;t<=qServersNumber;t++)
		{
			machines.add(new Server("Server #"+t));
		}
		
		solveLoadFactor();
	}
	
	private void solveIntensionProgramSolving()
	{
		for (oldProgram prog : Hystory) 
		{
			AverageTimeForSolving += (prog.Complexity / 100.0); 
		}
		AverageTimeForSolving = AverageTimeForSolving / Hystory.size();
		
		IntensionProgramSolving = 1 / AverageTimeForSolving;
	}
	
	/**Вычисление интенсивности потока*/
	private void solveIntensionProgramStream()
	{
		IntensionProgramStream = Mx() / (UpTime / 1000); //в сек.
	}
	private void solveLoadFactor() 
	{
		LoadFactor=IntensionProgramStream / IntensionProgramSolving;
	}
	public void update()
	{
		solveIntensionProgramSolving();
		solveIntensionProgramStream();
		solveLoadFactor();
		
		//if (qServersNumber==tmp) Undefined = true;
	}
	
	/** Вероятность того, что ВС не загружена */
	public Double SystemNotLoad_chance()
	{
		Double p0 = 1.0;
		Double Pa = 0.0;
		Double Pb = 0.0;
		
		for (int t=1;t<=qServersNumber;t++)
			p0+=s(LoadFactor, t)/f(t);
		
		Pa = s(LoadFactor, qServersNumber+1)/(qServersNumber*f(qServersNumber));
		
		Pb = (1-s((LoadFactor/qServersNumber),qBufferSize)) / (1-(LoadFactor/qServersNumber));
		
		p0 = p0 + Pa * Pb;
		
		return s(p0,-1);
	}
	
	/** Вероятность образования очереди*/
	public Double Queue_chance()
	{
		Double Pque = 0.0;
		Double Pup = 0.0;
		Double Pdn = 0.0;
		
		Pup = s(LoadFactor,qServersNumber)*(1- s(LoadFactor/qServersNumber,qBufferSize));
		Pdn = f(qServersNumber)*(1-(LoadFactor/qServersNumber));
		
		Pque = (Pup / Pdn) * SystemNotLoad_chance();
		
		return Pque;
	}
	
	/** Вероятность отказа в обслуживании*/
	public Double Decline_chance() 
	{
		Double Pdec = 0.0;
		Double Pup = 0.0;
		Double Pdn = 0.0;
		
		Pup = s(LoadFactor, qServersNumber+qBufferSize);
		Pdn = s(qServersNumber, qBufferSize)*f(qServersNumber);
		
		Pdec = (Pup / Pdn) * SystemNotLoad_chance();
		
		
		return Pdec;
	}
	
	/** Относительная пропускная способность*/
	public Double RelativeBandwidth()
	{
		return (double) (1-Decline_chance());
	}
	
	/** Абсолютная пропускная способность*/
	public Double AbsoluteBandwidth()
	{
		return (double)(IntensionProgramStream*RelativeBandwidth());
	}
	
	/** Среднее число программ в буфере */
	public Double AverageProgramsNumber_buffer()
	{
		Double Nbuf = 0.0;
		Double Na = 0.0;
		Double Nb = 0.0;
		Double N1,N2 = 0.0;
		Double Nup = 0.0;
		Double Ndn = 0.0;
		
		Nup = s(LoadFactor, qServersNumber+1);
		Ndn = qServersNumber * f(qServersNumber);
		
		Na = Nup / Ndn; //ok
		
		N1 = s(LoadFactor/qServersNumber,qBufferSize);
		N2 = 1+qBufferSize*(1-LoadFactor/qServersNumber);
		Ndn = s((1-LoadFactor/qServersNumber), 2);

		
		Nb = (1-(N1*N2))/Ndn;
		
		Nbuf = Na * Nb * SystemNotLoad_chance();

		return Nbuf;
	}
	
	/** Среднее число программ на сервере */
	public Double AverageProgramsNumber_server()
	{
		solveIntensionProgramSolving(); //update
		return AbsoluteBandwidth()/IntensionProgramSolving;
	}
	
	/** Среднее число программ в системе */
	public Double AverageProgramsNumber_system()
	{
		return AverageProgramsNumber_server()+AverageProgramsNumber_buffer();
	}
	
	/** Среднее время нахождения программ в системе */
	public Double AverageProgramsTimeOn_system()
	{
		return AverageProgramsNumber_system()/IntensionProgramStream;
	}
	
	/** Среднее время нахождения программ в буфере */
	public Double AverageProgramsTimeOn_buffer()
	{
		return AverageProgramsNumber_buffer()/IntensionProgramStream;
	}
	
//=================Special=================================
	
	/** Вероятность того, что загружен только 1 сервер */
	public Double ServerLoadOneOf_chance()
	{
		return (LoadFactor/f(1))*SystemNotLoad_chance();
	}
	/** Вероятность того, что загружены 2 сервера */
	public Double ServerLoadTwoOf_chance()
	{
		return (s(LoadFactor, 2)/f(2))*SystemNotLoad_chance();
	}	
	/** Вероятность того, что загружены 3 сервера */
	public Double ServerLoadThreeOf_chance()
	{
		return (s(LoadFactor, 3)/f(3))*SystemNotLoad_chance();
	}	
	/** Вероятность того, что загружены 4 сервера */
	public Double ServerLoadFourOf_chance()
	{
		return (s(LoadFactor, 4)/f(4))*SystemNotLoad_chance();
	}
	/** Вероятность того, в буфере 1 программа */
	public Double BufferLoadOneOf_chance()
	{
		return SystemNotLoad_chance() * (s(LoadFactor, 5) / ( s(qServersNumber, 1) * f(qServersNumber) ));
	}
	/** Вероятность того, в буфере 2 программы */
	public Double BufferLoadTwoOf_chance()
	{
		return SystemNotLoad_chance() * (s(LoadFactor, 6) / ( s(qServersNumber, 2) * f(qServersNumber) ));
	}
	/** Вероятность того, в буфере 3 программы */
	public Double BufferLoadThreeOf_chance()
	{
		return SystemNotLoad_chance() * (s(LoadFactor, 7) / ( s(qServersNumber, 3) * f(qServersNumber) ));
	}

//=================Utilities=================================
	/** Факториал */
	public static final Double f(Integer i)
	{
		Double tmp = 1.0;
		
		for (Integer t=1;t<=i;t++)
		{
			tmp *= t;
		}
		return tmp;
	}
	/** Возведение в  степень */
	@SuppressWarnings("unused")
	private static final Double s(Double in, Double stp)
	{
		return Math.pow(in, stp);
	}
	private static final Double s(Double in, int stp)
	{
		return Math.pow(in, stp);
	}
	private static final Double s(Integer in, Integer stp)
	{
		return Math.pow(in, stp);
	}
	
//=================Maintenance=================================
	
	/** Следующий такт системы */
	public void tick()
	{
		UpTime++;

		for (Server s : machines) 
		{
			s.tick();
			
			if ((!buffer.isEmpty()) && (!s.qBusy) )
			{
				s.Input(buffer.pollFirst());
			}
		}
		
		if (gInterval==0)
		{
			gNextProgram();
			gInterval=gLastProgram; // миллисекунд до след программы
		}
		else gInterval--;  //теряется 1мс но так безопаснее.

	}
	/**Математическое ожидание*/
	public Double Mx()
	{
		Double M = 0.0;
		Double Pos =  (1d / Hystory.size());
		
		for (oldProgram prog : Hystory) {
			M += prog.GenerationTime * Pos;
		}
		return M;
	}
	/**Сгенерировать следующую программу со случайной сложностью*/
	public void gNextProgram()
	{
		//Сложность программ. Фактически, сложность = время обработки
		//В условии задана слишком низкая скорость, поэтому программы не попадают в буфер
		//поэтому для наглядности сложность умножается на коэффициент. По-умолчанию он равен 100L
		Long NewProgram = 0l;
		
		if (ExponentialMode)
		{
			NewProgram = (long) (100L * Utils.rndExp(IntensionProgramSolving)); //диапазон задан условием (в сек.)
			gLastProgram = (long)(1000d * Utils.rndExp(IntensionProgramStream));
		}
		else
		{
			NewProgram = 1000L * Utils.rnd(1, 5); //диапазон задан условием (в сек.)
			gLastProgram = (long)(1000d * Utils.rnd(0.5, 0.833));
		}
		
		Hystory.add(new oldProgram(NewProgram,gLastProgram));
		
		for (Server s : machines) 
		{
			if (s.Input(NewProgram)) 
			{
				NewProgram = 0L;
				break; 
			}
		}
		
		if (NewProgram>0)
		{
			if (buffer.size()<4)
			{
				buffer.add(NewProgram);
			}
			else
			{
				DestroyedPrograms++;
				NewProgram = 0L;
				return;
			}
		}
	}
	
	/**Очистить историю*/
	public void clearHystory()
	{
		Hystory.clear();
	}
	/**Получить кол-во обработанных программ*/
	public Integer getSolvedProgramsNumber()
	{
		Integer count = 0;
		
		for (Server s : machines) 
		{
			count+=s.qCompletedTask;
		}
		return count;
	}
	/**Получить кол-во отклонённых серверами программ*/
	public Integer getDeclinedProgramsNumber()
	{
		Integer count = 0;
		for (Server s : machines) 
		{
			count+=s.qDeclined;
		}
		return count;
	}

	/**Получить кол-во программ в буфере*/
	public Integer getBufferedProgramsNumber()
	{
		return buffer.size();
	}

	/**Получить кол-во занятых серверов*/
	public Integer getBusyServersNumber()
	{
		Integer cnt = 0;
		
		for (Server server : machines) {
			if (server.qBusy) cnt++;
		}
		return cnt;
	}
}
