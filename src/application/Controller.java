package application;

import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class Controller {
	
	QESystem sys=new QESystem(4, 4);
	Long OneHour = 360000L;
	
	//Checkbox Mode
	@FXML private CheckBox ExponentialMode;
	
	//Common Components
	@FXML private Button GenerateButton;
	@FXML private Button GenerateOneHour;
	@FXML private Label ElapsedTime;
	@FXML private Label SolvedPrograms;
	
	//Table Variables
	@FXML private TableView<SystemData> SystemDataTable;
	@FXML private TableColumn<SystemData, String> PhysicalCol;
	@FXML private TableColumn<SystemData, String> AnalyticalCol;
	@FXML private TableColumn<SystemData, String> CalculatedCol;
	ObservableList<SystemData> list;
	
	//Chart Variables
	@FXML LineChart<Number,	Number> StatChart;
	@FXML NumberAxis xAxis;
	@FXML NumberAxis yAxis;
	
	Series<Number, Number> BufferSeries = new XYChart.Series<>();
	Series<Number, Number> BusySeries = new XYChart.Series<>();
	ObservableList<Data<Number, Number>> BufferList;
	ObservableList<Data<Number, Number>> BusyList;
	
	Integer stepTime;
	Integer memoryTime;
	
	@FXML
    public void initialize() {
		
		//TableSettings
        PhysicalCol.setCellValueFactory(new PropertyValueFactory<>("PhysicalData"));
        AnalyticalCol.setCellValueFactory(new PropertyValueFactory<>("AnalyticalData"));
        CalculatedCol.setCellValueFactory(new PropertyValueFactory<>("CalculatedData"));
        
        list = getSystemData(); //
        
        SystemDataTable.setItems(list);
        
        //ChartSettings
        
        stepTime=0;
        xAxis.setLabel("�����");
        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(true);
        xAxis.setAnimated(false);
        
        yAxis.setLabel("���������");
        yAxis.setForceZeroInRange(false);
        yAxis.setAnimated(false);
        //yAxis.setUpperBound(10.0);
        //yAxis.setAutoRanging(false);
        StatChart.setTitle("Servers statistics");
        StatChart.setCreateSymbols(false);
        StatChart.setAnimated(false);
        StatChart.setCenterShape(true);


        BufferSeries.setName("�����");
        BusySeries.setName("������� �������");
        

        
        //�������� ������ ����� ����������
        InitSeries(BufferSeries);
        InitSeries(BusySeries);
        
        BufferList = BufferSeries.getData(); 
		BusyList = BusySeries.getData();
		

        plot();
        StatChart.getData().add(BufferSeries);
        StatChart.getData().add(BusySeries);
    }
	
	Timeline clock = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {

	    @Override
	    public void handle(ActionEvent event) {
	        sys.tick();
	        ElapsedTime.setText("������ �������: "+sys.UpTime+" ��");
	        SolvedPrograms.setText("���������� ��������: "+sys.getSolvedProgramsNumber());
	        UpdateData();
	        plot();
	    }
	}));
	
	
	private void plot()
	{
		stepTime++;
		Integer b = null; b = sys.buffer.size();
		Integer s = null; s = sys.getBusyServersNumber();
		
		if(stepTime>1) 
		{ 
			xAxis.setLowerBound((sys.UpTime-99)/100);
			xAxis.setUpperBound((sys.UpTime+1)/100);
			
			
		}

		shiftSeries(BufferSeries, (sys.UpTime)/100, b);
		shiftSeries(BusySeries, (sys.UpTime)/100, s);
		
		if (stepTime>2) 
		{ 
			stepTime=0; 
		}
		
	
	}
	public ObservableList<SystemData> getSystemData()
	{
		ArrayList<SystemData> sdl = new ArrayList<>();
		
		for (int i=0;i<=22;i++)
			sdl.add(new SystemData("", "", ""));
		
		ObservableList<SystemData> lst = null;
		lst = FXCollections.observableArrayList(sdl);
		
		return lst;
	}
	
	public void UpdateData() {
		list.get(0).setPhysicalData("��������� �����: "+sys.UpTime);
		list.get(1).setPhysicalData("����������� ���������: "+sys.getSolvedProgramsNumber());
		list.get(2).setPhysicalData("�������� � ������: "+sys.getBufferedProgramsNumber());
		list.get(3).setPhysicalData("�������� ��������� ���������: "+sys.getDeclinedProgramsNumber());
		list.get(4).setPhysicalData("�������� ��������(����� �����): "+sys.DestroyedPrograms);
		list.get(5).setPhysicalData("������["+sys.machines.get(0).qName+"] �����: "+sys.machines.get(0).qBusy);
		list.get(6).setPhysicalData("������["+sys.machines.get(0).qName+"] ���������: "+sys.machines.get(0).qCompletedTask);
		list.get(7).setPhysicalData("������["+sys.machines.get(0).qName+"] ���������: "+sys.machines.get(0).qDeclined);
		list.get(8).setPhysicalData("������["+sys.machines.get(1).qName+"] �����: "+sys.machines.get(1).qBusy);
		list.get(9).setPhysicalData("������["+sys.machines.get(1).qName+"] ���������: "+sys.machines.get(1).qCompletedTask);
		list.get(10).setPhysicalData("������["+sys.machines.get(1).qName+"] ���������: "+sys.machines.get(1).qDeclined);
		list.get(11).setPhysicalData("������["+sys.machines.get(2).qName+"] �����: "+sys.machines.get(2).qBusy);
		list.get(12).setPhysicalData("������["+sys.machines.get(2).qName+"] ���������: "+sys.machines.get(2).qCompletedTask);
		list.get(13).setPhysicalData("������["+sys.machines.get(2).qName+"] ���������: "+sys.machines.get(2).qDeclined);
		list.get(14).setPhysicalData("������["+sys.machines.get(3).qName+"] �����: "+sys.machines.get(3).qBusy);
		list.get(15).setPhysicalData("������["+sys.machines.get(3).qName+"] ���������: "+sys.machines.get(3).qCompletedTask);
		list.get(16).setPhysicalData("������["+sys.machines.get(3).qName+"] ���������: "+sys.machines.get(3).qDeclined);
		
		
		SystemDataTable.refresh();
	}
	
	public void UpdateAnalyticData() {
		
		list.get(0).setAnalyticalData("���-�� ��������: " + sys.qServersNumber);
		list.get(1).setAnalyticalData("������ ������: " + sys.qBufferSize);
		list.get(2).setAnalyticalData("�����.������.����.: " + sys.IntensionProgramStream);
		list.get(3).setAnalyticalData("��.����� ���.����.:  " + sys.AverageTimeForSolving);
		list.get(4).setAnalyticalData("�����.���.����.����.: " + sys.IntensionProgramSolving);
		list.get(5).setAnalyticalData("����. ��������: " + sys.LoadFactor);
		
		list.get(6).setAnalyticalData("���.��� �� �� ���������: " + sys.SystemNotLoad_chance());
		list.get(7).setAnalyticalData("���. �����.�������: " + sys.Queue_chance());
		list.get(8).setAnalyticalData("���. ������ � ������.: " + sys.Decline_chance());
		list.get(9).setAnalyticalData("�����.�������.�����������: " + sys.RelativeBandwidth());
		list.get(10).setAnalyticalData("�����.�������.�����������: " + sys.AbsoluteBandwidth());
		list.get(11).setAnalyticalData("��.����� ����.� ������: " + sys.AverageProgramsNumber_buffer());
		list.get(12).setAnalyticalData("��.����� ����.�� ��������: " + sys.AverageProgramsNumber_server());
		list.get(13).setAnalyticalData("��.����� ����.� �������: " + sys.AverageProgramsNumber_system());
		list.get(14).setAnalyticalData("��.����� ����.� �������: " + sys.AverageProgramsTimeOn_system());
		list.get(15).setAnalyticalData("��.����� ����.� ������: " + sys.AverageProgramsTimeOn_buffer());
		list.get(16).setAnalyticalData("���.��� ����.���� ����.: " + sys.ServerLoadOneOf_chance());
		list.get(17).setAnalyticalData("���.��� ����.��� ����.: " + sys.ServerLoadTwoOf_chance());
		list.get(18).setAnalyticalData("���.��� ����.��� ����.: " + sys.ServerLoadThreeOf_chance());
		list.get(19).setAnalyticalData("���.��� ����.������ ����.: " + sys.ServerLoadFourOf_chance());
		list.get(20).setAnalyticalData("���.��� � ���.���� ����.: " + sys.BufferLoadOneOf_chance());
		list.get(21).setAnalyticalData("���.��� � ���.��� ����.: " + sys.BufferLoadTwoOf_chance());
		list.get(22).setAnalyticalData("���.��� � ���.��� ����.: " + sys.BufferLoadThreeOf_chance());
		
		SystemDataTable.refresh();
	}
	public void UpdateCalculateData()
	{
		list.get(0).setCalculatedData("���-�� ��������: " + sys.qServersNumber);
		list.get(1).setCalculatedData("������ ������: " + sys.qBufferSize);
		list.get(2).setCalculatedData("�����.������.����.: " + sys.IntensionProgramStream);
		list.get(3).setCalculatedData("��.����� ���.����.:  " + sys.AverageTimeForSolving);
		list.get(4).setCalculatedData("�����.���.����.����.: " + sys.IntensionProgramSolving);
		list.get(5).setCalculatedData("����. ��������: " + sys.LoadFactor);
		
		list.get(6).setCalculatedData("���.��� �� �� ���������: " + sys.SystemNotLoad_chance());
		list.get(7).setCalculatedData("���. �����.�������: " + sys.Queue_chance());
		list.get(8).setCalculatedData("���. ������ � ������.: " + sys.Decline_chance());
		list.get(9).setCalculatedData("�����.�������.�����������: " + sys.RelativeBandwidth());
		list.get(10).setCalculatedData("�����.�������.�����������: " + sys.AbsoluteBandwidth());
		list.get(11).setCalculatedData("��.����� ����.� ������: " + sys.AverageProgramsNumber_buffer());
		list.get(12).setCalculatedData("��.����� ����.�� ��������: " + sys.AverageProgramsNumber_server());
		list.get(13).setCalculatedData("��.����� ����.� �������: " + sys.AverageProgramsNumber_system());
		list.get(14).setCalculatedData("��.����� ����.� �������: " + sys.AverageProgramsTimeOn_system());
		list.get(15).setCalculatedData("��.����� ����.� ������: " + sys.AverageProgramsTimeOn_buffer());
		list.get(16).setCalculatedData("���.��� ����.���� ����.: " + sys.ServerLoadOneOf_chance());
		list.get(17).setCalculatedData("���.��� ����.��� ����.: " + sys.ServerLoadTwoOf_chance());
		list.get(18).setCalculatedData("���.��� ����.��� ����.: " + sys.ServerLoadThreeOf_chance());
		list.get(19).setCalculatedData("���.��� ����.������ ����.: " + sys.ServerLoadFourOf_chance());
		list.get(20).setCalculatedData("���.��� � ���.���� ����.: " + sys.BufferLoadOneOf_chance());
		list.get(21).setCalculatedData("���.��� � ���.��� ����.: " + sys.BufferLoadTwoOf_chance());
		list.get(22).setCalculatedData("���.��� � ���.��� ����.: " + sys.BufferLoadThreeOf_chance());
		
		SystemDataTable.refresh();
	}
	@FXML
	public void Processing()
	{
		GenerateButton.setText("...");

		clock.setCycleCount(Timeline.INDEFINITE);
		clock.play();

		/*Alert alert = new Alert(Alert.AlertType.INFORMATION);
		//alert.setTitle("Testing!");
		//alert.setHeaderText("Value test message!");
		//alert.setContentText(""+ sys); //ok
		//alert.setContentText("s(6,24): "+ sys.s(6,24)); //ok
		//alert.setContentText("Factorial 4!: "+ sys.f(4));//ok
		//alert.setContentText("LoadFactor: "+ sys.LoadFactor);//ok
		//alert.setContentText("IntensionStream: "+ sys.IntensionProgramSolving);//ok
		//alert.setContentText("UnloadChance: "+ sys.SystemNotLoad_chance());//ok
		//alert.setContentText("Queue_chance(): "+ sys.Queue_chance()); //ok
		//alert.setContentText("Decline_chance(): "+ sys.Decline_chance()); //ok
		//alert.setContentText("RelativeBandwidth(): "+ sys.RelativeBandwidth()); //ok
		//alert.setContentText("AbsoluteBandwidth(): "+ sys.AbsoluteBandwidth()); //ok
		//alert.setContentText("AverageProgramsNumber_buffer(): "+ sys.AverageProgramsNumber_buffer()); //ok
		//alert.setContentText("AverageProgramsNumber_server(): "+ sys.AverageProgramsNumber_server()); //ok
		//alert.setContentText("AverageProgramsNumber_system: "+ sys.AverageProgramsNumber_system()); //ok
		//alert.setContentText("AverageProgramsTimeOn_system(): "+ sys.AverageProgramsTimeOn_system()); //ok
		//alert.setContentText("AverageProgramsTimeOn_buffer(): "+ sys.AverageProgramsTimeOn_buffer()); //ok
		//alert.setContentText("ServerLoadOneOf_chance()"+ sys.ServerLoadOneOf_chance()); //ok
		//alert.setContentText("ServerLoadTwoOf_chance()"+ sys.ServerLoadTwoOf_chance()); //ok
		//alert.setContentText("ServerLoadThreeOf_chance()"+ sys.ServerLoadThreeOf_chance()); //ok
		//alert.setContentText("ServerLoadFourOf_chance()"+ sys.ServerLoadFourOf_chance()); //ok
		//alert.setContentText("BufferLoadOneOf_chance()"+ sys.BufferLoadOneOf_chance()); //ok
		//alert.setContentText("BufferLoadTwoOf_chance()"+ sys.BufferLoadTwoOf_chance()); //ok
		//alert.setContentText("BufferLoadThreeOf_chance()"+ sys.BufferLoadThreeOf_chance()); //ok
		//alert.setContentText("Random between 1 and 5:  "+ Utils.rnd(1, 5)); //ok
		//alert.setContentText("Random between 1/2 and 5/6:  "+ Utils.rnd(0.5, 0.833)); //ok
		//alert.showAndWait();
		*/
	}
	@FXML
	public void GenerateButton_onRelease()
	{
		GenerateButton.setText("������������");
		clock.stop();
		ElapsedTime.setText("������ �������: "+sys.UpTime+" ��");
        SolvedPrograms.setText("���������� ��������: "+sys.getSolvedProgramsNumber());

        System.gc();
        
        sys.update(); //��������� ������
        UpdateCalculateData();
        UpdateAnalyticData();
        sys.clearHystory();

        
	}
	
	@FXML
	public void GenerateOneHour_onClick()
	{
		while (sys.UpTime<OneHour) 
		{
			sys.tick();
			ElapsedTime.setText("������ �������: "+sys.UpTime+" ��");
	        SolvedPrograms.setText("���������� ��������: "+sys.getSolvedProgramsNumber());
	        UpdateData();
	        
		}
        UpdateAnalyticData();
		sys.update(); //��������� ������
        UpdateCalculateData();
	}
	

	public void shiftSeries (XYChart.Series series, long newXValue, long newYValue)
	{
		int Length = series.getData().size();
		
		for (int i = 0; i<Length-1; i++)
		{
			XYChart.Data<Number, Number> data = (XYChart.Data<Number, Number>) series.getData().get(i);
			XYChart.Data<Number, Number> next_data = (XYChart.Data<Number, Number>) series.getData().get(i+1);
			data.setXValue(next_data.getXValue());
			data.setYValue(next_data.getYValue());
		}
		
		XYChart.Data<Number, Number> last_data = (XYChart.Data<Number, Number>) series.getData().get(Length-1);
		last_data.setXValue(newXValue);
		last_data.setYValue(newYValue);
    }
	
	public void InitSeries(XYChart.Series series)
	{
		//�������� ������ �� ����� ������������� �������. ����� ��� �������� ������.
		int StartLength = 700; 
		
		for (int i = 0; i<StartLength; i++)
		{
			series.getData().add(new XYChart.Data(i,0));
		}

	}
	public void ExponentialMode_Checked()
	{
		sys.ExponentialMode = ExponentialMode.isSelected();
	}
	
}
