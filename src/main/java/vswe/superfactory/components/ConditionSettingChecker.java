package vswe.superfactory.components;


public class ConditionSettingChecker {
	private int     amount;
	private Setting setting;

	public ConditionSettingChecker(Setting setting) {
		this.setting = setting;
		amount = 0;
	}

	public void addCount(int n) {
		amount += n;
	}

	public boolean isTrue() {
		return !setting.isLimitedByAmount() || amount >= setting.getAmount();
	}
}
