import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Tabs from "@material-ui/core/Tabs";
import Tab from "@material-ui/core/Tab";
import Typography from "@material-ui/core/Typography";
import Box from "@material-ui/core/Box";
import Paper from "@material-ui/core/Paper";
import Breadcrumbs from "@material-ui/core/Breadcrumbs";
import Link from "@material-ui/core/Link";
import { MdDashboard, MdContacts } from "react-icons/md";
import ErrorIcon from "@mui/icons-material/Error";
import CloudDownloadIcon from "@mui/icons-material/CloudDownload";
import SettingsIcon from "@mui/icons-material/Settings";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import FingerprintIcon from "@mui/icons-material/Fingerprint";
import HomeIcon from "@mui/icons-material/Home";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import GenerateNdr from "./generate";
import HTS from "./hts";
import Prep from "./Prep";
import Pmtct from "./Pmtct";
import DownloadNdr from "./download";
import Biometrics from "./Biometrics";
import ErrorPage from "./Errors";
import NdrConfiguration from "./NdrConfiguration";
import Redact from "./Redact";
import { getQueryParams } from "./../PageUtils";

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`scrollable-force-tabpanel-${index}`}
      aria-labelledby={`scrollable-force-tab-${index}`}
      {...other}
    >
      {value === index && <Box p={3}>{children}</Box>}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

function a11yProps(index) {
  return {
    id: `scrollable-force-tab-${index}`,
    "aria-controls": `scrollable-force-tabpanel-${index}`,
  };
}

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: "100%",
    backgroundColor: "#f5f7fa",
    minHeight: "100vh",
  },
  breadcrumbBar: {
    backgroundColor: "#fff",
    padding: theme.spacing(1.5, 3),
    borderBottom: "1px solid #e0e0e0",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },
  breadcrumbTitle: {
    fontSize: "1.25rem",
    fontWeight: 600,
    color: "#014d88",
    margin: 0,
  },
  breadcrumbLink: {
    display: "flex",
    alignItems: "center",
    color: "#5c6bc0",
    textDecoration: "none",
    fontSize: "0.85rem",
    "&:hover": {
      textDecoration: "underline",
      color: "#014d88",
    },
  },
  appBar: {
    backgroundColor: "#fff",
    boxShadow: "0 1px 4px rgba(0,0,0,0.08)",
    borderBottom: "1px solid #e8eaf6",
  },
  tabs: {
    "& .MuiTabs-indicator": {
      backgroundColor: "#014d88",
      height: 3,
      borderRadius: "3px 3px 0 0",
    },
    "& .MuiTab-root": {
      textTransform: "none",
      fontWeight: 500,
      fontSize: "0.82rem",
      minWidth: 120,
      color: "#5f6368",
      transition: "all 0.2s ease",
      padding: theme.spacing(1.5, 2),
      "&:hover": {
        backgroundColor: "#f0f2ff",
        color: "#014d88",
      },
    },
    "& .Mui-selected": {
      color: "#014d88 !important",
      fontWeight: 600,
    },
  },
  tabIcon: {
    fontSize: "1.25rem",
    marginBottom: theme.spacing(0.5),
  },
  contentArea: {
    padding: theme.spacing(2, 3),
    backgroundColor: "#f5f7fa",
    minHeight: "calc(100vh - 180px)",
  },
  tabContentWrapper: {
    backgroundColor: "#fff",
    borderRadius: 8,
    boxShadow: "0 1px 3px rgba(0,0,0,0.06)",
    overflow: "hidden",
  },
}));

const tabsConfig = [
  { label: "Treatment XML", icon: <MdDashboard />, component: GenerateNdr },
  { label: "HTS XML", icon: <MdContacts />, component: HTS },
  { label: "PrEP XML", icon: <MdContacts />, component: Prep },
  { label: "PMTCT XML", icon: <MdContacts />, component: Pmtct },
  {
    label: "Biometrics Recapture",
    icon: <FingerprintIcon />,
    component: Biometrics,
  },
  {
    label: "Download Files",
    icon: <CloudDownloadIcon />,
    component: DownloadNdr,
  },
  { label: "NDR File Errors", icon: <ErrorIcon />, component: ErrorPage },
  {
    label: "NDR Configuration",
    icon: <SettingsIcon />,
    component: NdrConfiguration,
  },
  { label: "NDR Redacted", icon: <VisibilityOffIcon />, component: Redact },
];

const HomePage = (props) => {
  const classes = useStyles();
  const urlIndex = getQueryParams("tab", props.location);
  const initialTab =
    urlIndex !== null && !isNaN(Number(urlIndex)) ? Number(urlIndex) : 0;
  const [value, setValue] = useState(initialTab);
  const [errors, setErrors] = useState([]);

  useEffect(() => {
    if (
      urlIndex !== null &&
      !isNaN(Number(urlIndex)) &&
      Number(urlIndex) !== value
    ) {
      setValue(Number(urlIndex));
    }
  }, [urlIndex]);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  return (
    <div className={classes.root}>
      <div className={classes.breadcrumbBar}>
        <div>
          <Breadcrumbs
            separator={
              <NavigateNextIcon fontSize="small" style={{ color: "#9e9e9e" }} />
            }
            aria-label="breadcrumb"
          >
            <Link className={classes.breadcrumbLink} href="/">
              <HomeIcon style={{ fontSize: 16, marginRight: 4 }} />
              Home
            </Link>
            <Typography style={{ fontSize: "0.85rem", color: "#757575" }}>
              NDR
            </Typography>
          </Breadcrumbs>
          <h4 className={classes.breadcrumbTitle}>
            NDR Data Exchange Management
          </h4>
        </div>
        <Typography variant="caption" style={{ color: "#9e9e9e" }}>
          National Data Repository
        </Typography>
      </div>

      {/* Tabs */}
      <AppBar position="static" className={classes.appBar} elevation={0}>
        <Tabs
          value={value}
          onChange={handleChange}
          variant="scrollable"
          scrollButtons="auto"
          indicatorColor="primary"
          textColor="primary"
          aria-label="NDR navigation tabs"
          className={classes.tabs}
        >
          {tabsConfig.map((tab, index) => (
            <Tab
              key={index}
              label={tab.label}
              icon={tab.icon}
              {...a11yProps(index)}
            />
          ))}
        </Tabs>
      </AppBar>

      {/* Content */}
      <div className={classes.contentArea}>
        <Paper className={classes.tabContentWrapper} elevation={0}>
          {tabsConfig.map((tab, index) => {
            const Component = tab.component;
            if (index === 5) {
              return (
                <TabPanel
                  key={index}
                  value={value}
                  setValue={setValue}
                  index={index}
                >
                  <Component
                    value={value}
                    setValue={setValue}
                    setErrors={setErrors}
                  />
                </TabPanel>
              );
            }
            if (index === 6) {
              return (
                <TabPanel
                  key={index}
                  value={value}
                  setValue={setValue}
                  index={index}
                >
                  <Component
                    value={value}
                    setValue={setValue}
                    errors={errors}
                  />
                </TabPanel>
              );
            }
            return (
              <TabPanel
                key={index}
                value={value}
                setValue={setValue}
                index={index}
              >
                <Component value={value} setValue={setValue} />
              </TabPanel>
            );
          })}
        </Paper>
      </div>
    </div>
  );
};

export default HomePage;
