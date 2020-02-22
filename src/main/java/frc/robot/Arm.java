package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANPIDController.AccelStrategy;
import edu.wpi.first.wpilibj.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

import org.hotutilites.hotInterfaces.IHotSensedActuator;



public class Arm implements IHotSensedActuator<RobotState, RobotCommandProvider, Integer > {


    private TalonSRX armMotor;
    public boolean hasReset = false;

    private double currentDegreesFromHor;
    private double cosineScalar;
    private double currentCurrent;
    private int counter = 0;
    private double output;


    public Arm(){

        armMotor = new TalonSRX(Calibrations.CAN_ID.intakeLifter);
        armMotor.configFactoryDefault();
        
        

        armMotor.setInverted(false);
        armMotor.setSensorPhase(true);
        armMotor.setNeutralMode(NeutralMode.Brake);
        // armMotor.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Absolute, 20);
        armMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 40);
    
     
        armMotor.configPeakCurrentLimit(40, 20); //Limit of 45 amps
        armMotor.configPeakCurrentDuration(500, 20); //For 500ms
        armMotor.configContinuousCurrentLimit(30, 20); //Limit of 30 amps cont

        armMotor.configNominalOutputForward(0, 0);
		armMotor.configNominalOutputReverse(0, 0);
		armMotor.configPeakOutputForward(1, 0);
		armMotor.configPeakOutputReverse(-1, 0);



        //config motion magic
    //distance profile and primarily closed
        armMotor.configNeutralDeadband(0.1, 30);
        armMotor.selectProfileSlot(0, 0); 

        armMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 40);
        armMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 40);

        armMotor.configMotionAcceleration(Calibrations.ARM.acc, 30);                
        armMotor.configMotionCruiseVelocity(Calibrations.ARM.vel, 30);
        armMotor.config_kP(0, Calibrations.ARM.kP, 30);             //0 = parameter slot, 0 is distance mode
		armMotor.config_kI(0, Calibrations.ARM.kI, 30);
        armMotor.config_kD(0, Calibrations.ARM.kD, 30);
        armMotor.config_kF(0, Calibrations.ARM.kF, 30);
          
        zeroSensor();
    }

    public enum ArmPositions{      
        packaged,
        wallshot,
        autoshot,
        trenchshot,
        ground,
        manual,
        noCommand
    }

    public enum ArmStates{            
        changingPosition,
        atPosition,
        resetting,
        freefall                        //nothing commanded, gravity controlled. (should be default so robot doesn't drop when disabled, safety risk, position button held at all time?)
    }
    

    public void performAction(RobotCommandProvider commander, RobotState state){

      
        SmartDashboard.putNumber("current Velocity", armMotor.getSelectedSensorVelocity());
        SmartDashboard.putNumber("ticks ", armMotor.getSelectedSensorPosition());
        SmartDashboard.putString("commanded pos ", commander.getArmPosition().toString());
        SmartDashboard.putNumber("commanded output", armMotor.getMotorOutputPercent());
        SmartDashboard.putNumber("motion magc error", armMotor.getClosedLoopError(0));
        
  
        

        //if (!commander.getArmReset() && hasReset) {
        
           

        switch(commander.getArmPosition()){
            case packaged:
                armMotor.set(ControlMode.MotionMagic, Calibrations.ArmPositions.packagedAngle*Calibrations.ARM.ticksPerDegree);
                // armMotor.set(ControlMode.MotionMagic, Calibrations.ARM.packagedAngleSetPoint*Calibrations.ARM.ticksPerDegree, DemandType.ArbitraryFeedForward, calcArbFF());
            break;                                                                  //ticks per degree prob 360/4096 * gear ratio?
            case wallshot:
                armMotor.set(ControlMode.MotionMagic,  Calibrations.ArmPositions.wallShotAngle*Calibrations.ARM.ticksPerDegree);
            
              // armMotor.set(ControlMode.MotionMagic, Calibrations.ARM.visionAngleSetPoint*Calibrations.ARM.ticksPerDegree, DemandType.ArbitraryFeedForward, calcArbFF()); 
            break;
            case autoshot:
                armMotor.set(ControlMode.MotionMagic,  Calibrations.ArmPositions.autoShotAngle*Calibrations.ARM.ticksPerDegree);
            
              // armMotor.set(ControlMode.MotionMagic, Calibrations.ARM.visionAngleSetPoint*Calibrations.ARM.ticksPerDegree, DemandType.ArbitraryFeedForward, calcArbFF()); 
            break;
            case trenchshot:
                armMotor.set(ControlMode.MotionMagic,  Calibrations.ArmPositions.trenchShotAngle*Calibrations.ARM.ticksPerDegree);
            
              // armMotor.set(ControlMode.MotionMagic, Calibrations.ARM.visionAngleSetPoint*Calibrations.ARM.ticksPerDegree, DemandType.ArbitraryFeedForward, calcArbFF()); 
            break;
            case ground:
                  armMotor.set(ControlMode.MotionMagic, Calibrations.ArmPositions.groundPickupAngle*Calibrations.ARM.ticksPerDegree);
               // armMotor.set(ControlMode.MotionMagic, Calibrations.ARM.groundAngleSetPoint*Calibrations.ARM.ticksPerDegree, DemandType.ArbitraryFeedForward, calcArbFF()); 
            break;
            case manual:
                armMotor.set(ControlMode.PercentOutput, commander.getArmOutput());
            break;
            case noCommand:

            break;
            }

            
        }
        

        // if(commander.getArmReset()) {
        //     if (!hardResetComplete()){              //runs hard reset
        //         state.setArmState(ArmStates.resetting);
        //     }

        //     else{
        //         state.setArmState(ArmStates.freefall);        //can log
        //         hasReset = true;
        //     }
        // }
    // }


    public boolean hardResetComplete(){
      
        currentCurrent = Math.abs(armMotor.getStatorCurrent());
        SmartDashboard.putNumber("current current", currentCurrent);
        if  (currentCurrent > Calibrations.ARM.averageCurrentDraw + 5){    
            armMotor.set(ControlMode.PercentOutput, 0.00);  
            armMotor.setSelectedSensorPosition(0);
            counter = 0;
            return true;
        }
        else {
           armMotor.set(ControlMode.PercentOutput, -0.3);     //need to determine arm motor output direction
            counter++;
           
            return false;
        }
        
    }

    private double calcArbFF(){
        currentDegreesFromHor = (armMotor.getSelectedSensorPosition()/ Calibrations.ARM.ticksPerDegree);
        cosineScalar = java.lang.Math.cos(Math.toRadians(currentDegreesFromHor));
        return Calibrations.ARM.maxGravityFF * cosineScalar;     //maxGravFF = output to apply when arm is horizontal

    }

    


    @Override
    public void updateState() {
        // TODO Auto-generated method stub

    }

    @Override
    public void zeroSensor() {
        armMotor.set(ControlMode.PercentOutput, 0.00);  
        armMotor.setSelectedSensorPosition(0, 0, 0);

    }

    @Override
    public void setSensorValue(Integer value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRobotState(RobotState robotState) {
        // TODO Auto-generated method stub

    }

  
}       