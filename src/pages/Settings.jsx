import SettingsForm from "../components/SettingsForm";
import { Card, Typography } from "antd";
import "../css/Settings.css";

const { Title, Text } = Typography;

function Settings() {
  return (
    <div className="settings-bg">
      <Card className="settings-card settings-card-body">
        <div className="settings-title">
          <Title level={2} className="settings-title-main">
            个人设置
          </Title>
          <Text type="secondary" className="settings-title-sub">
            完善你的个人信息，打造专属形象
          </Text>
        </div>
        <SettingsForm />
      </Card>
    </div>
  );
}
export default Settings;