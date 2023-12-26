package io.openems.edge.e3dc;

import io.openems.edge.bridge.modbus.api.element.SignedWordElement;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.meter.api.MeterType;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.common.component.OpenemsComponent;

@Designate(ocd = Config.class, factory = true)
@Component(//
    name = "Meter.E3DC.Grid", //
    immediate = true, //
    configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class E3dcGridMeterImpl extends AbstractOpenemsModbusComponent implements E3dcGridMeter, ModbusComponent, OpenemsComponent, ElectricityMeter {

    @Reference
    private ConfigurationAdmin cm;

    @Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
    protected void setModbus(BridgeModbus modbus) {
        super.setModbus(modbus);
    }

    private Config config = null;

    public E3dcGridMeterImpl() {
        super(//
            OpenemsComponent.ChannelId.values(), //
			ElectricityMeter.ChannelId.values(), //
			ModbusComponent.ChannelId.values(), //
            E3dcGridMeter.ChannelId.values() //
        );
    }

    @Activate
    private void activate(ComponentContext context, Config config) throws OpenemsException {
        if (super.activate(context, config.id(), config.alias(), config.enabled(), config.modbusUnitId(), this.cm, "Modbus",
            config.modbus_id())) {
            return;
        }
        this.config = config;
    }

    @Override
    @Deactivate
    protected void deactivate() {
        super.deactivate();
    }

    @Override
    protected ModbusProtocol defineModbusProtocol() throws OpenemsException {
        return new ModbusProtocol(this,
			new FC3ReadRegistersTask(40074,
				Priority.HIGH,
				m(ElectricityMeter.ChannelId.ACTIVE_POWER, new SignedWordElement(40074))));
    }

    @Override
    public String debugLog() {
        return "L:" + this.getActivePower().asString();
    }

	@Override
	public MeterType getMeterType() {
		return this.config.type();
	}
}
