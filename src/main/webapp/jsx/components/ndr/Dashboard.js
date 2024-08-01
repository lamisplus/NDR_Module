import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { makeStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Tabs from "@material-ui/core/Tabs";
import Tab from "@material-ui/core/Tab";
import Typography from "@material-ui/core/Typography";
import Box from "@material-ui/core/Box";
import { MdDashboard, MdContacts } from "react-icons/md";
import ErrorIcon from "@mui/icons-material/Error";
import GenerateNdr from "./generate";
import HTS from "./hts";
import DownloadNdr from "./download";
import Biometrics from "./Biometrics";
import ErrorPage from "./Errors";
import NdrConfiguration from "./NdrConfiguration";
import Redact from "./Redact";
import { getQueryParams } from "./../PageUtils";

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <Typography
      component="div"
      role="tabpanel"
      hidden={value !== index}
      id={`scrollable-force-tabpanel-${index}`}
      aria-labelledby={`scrollable-force-tab-${index}`}
      {...other}
    >
      {value === index && <Box p={5}>{children}</Box>}
    </Typography>
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
  root2: {
    flexGrow: 1,
    width: "100%",
    backgroundColor: theme.palette.background.paper,
    margin: theme.spacing(7),
    bullet: {
      display: "inline-block",
      margin: "0 2px",
      transform: "scale(0.8)",
    },
    title: {
      fontSize: 12,
    },
    pos: {
      fontSize: 11,
    },
    cardContent: {
      padding: 2,
    },
    cardroot: {
      margin: theme.spacing(1),
      height: 250 + "px !important",
    },
  },
  alertmsge: {
    marginTop: theme.spacing(2),
  },
  rootaccordia: {
    width: "100%",
  },
  accordiaheading: {
    fontSize: theme.typography.pxToRem(15),
    fontWeight: theme.typography.fontWeightRegular,
  },
  allergiesroot: {
    display: "flex",
    justifyContent: "center",
    flexWrap: "wrap",
    "& > *": {
      margin: theme.spacing(0.5),
    },
  },

  checkboxroot: {
    display: "flex",
  },
  formControl: {
    margin: theme.spacing(3),
  },

  root: {
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: 200,
    },
  },

  formroot: {
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: 200,
    },
  },

  heading: {
    fontSize: theme.typography.pxToRem(15),
  },
  secondaryHeading: {
    fontSize: theme.typography.pxToRem(15),
    color: theme.palette.text.secondary,
  },
  icon: {
    verticalAlign: "bottom",
    height: 20,
    width: 20,
  },
  details: {
    alignItems: "center",
  },
  column: {
    flexBasis: "33.33%",
  },
  helper: {
    borderLeft: `2px solid ${theme.palette.divider}`,
    padding: theme.spacing(1, 2),
  },
  link: {
    color: theme.palette.primary.main,
    textDecoration: "none",
    "&:hover": {
      textDecoration: "underline",
    },
  },
  inforoot: {
    width: "95%",
    margin: 20,
    backgroundColor: "#eee",
  },
}));

const HomePage = (props) => {
  const classes = useStyles();
  const [value, setValue] = useState(0);
  const urlIndex = getQueryParams("tab", props.location);
  const [errors, setErrors] = useState([]);
  //const urlTabs = urlIndex !== null ? urlIndex : props.location ;
  //
  useEffect(() => {
    //using the value to control the tabs
  }, [value]);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  return (
    <div className={classes.root}>
      <div
        className="row page-titles mx-0"
        style={{ marginTop: "0px", marginBottom: "-10px" }}
      >
        <ol className="breadcrumb">
          <li className="breadcrumb-item active">
            <h4>NDR</h4>
          </li>
        </ol>
      </div>
      <br />

      <AppBar position="static" style={{ backgroundColor: "#fff" }}>
        <Tabs
          value={value}
          onChange={handleChange}
          variant="scrollable"
          scrollButtons="on"
          indicatorColor="secondary"
          textColor="primary"
          aria-label="scrollable force tabs example"
        >
          <Tab
            className={classes.title}
            label="Treatment XML"
            icon={<MdDashboard />}
            {...a11yProps(0)}
          />

          <Tab
            className={classes.title}
            label="HTS XML"
            icon={<MdContacts />}
            {...a11yProps(1)}
          />

          <Tab
            className={classes.title}
            label="Biometrics Recapture XML"
            icon={<MdContacts />}
            {...a11yProps(2)}
          />
          <Tab
            className={classes.title}
            label="Download Files"
            icon={<MdContacts />}
            {...a11yProps(3)}
          />
          <Tab
            className={classes.title}
            label="NDR File Errors"
            icon={<ErrorIcon />}
            {...a11yProps(4)}
          />
          <Tab
            className={classes.title}
            label="NDR COnfiguration"
            icon={<MdContacts />}
            {...a11yProps(5)}
          />
          <Tab
            className={classes.title}
            label="NDR Redacted"
            icon={<MdContacts />}
            {...a11yProps(6)}
          />
        </Tabs>
        <div></div>
      </AppBar>

      <TabPanel value={value} setValue={setValue} index={0}>
        <GenerateNdr value={value} setValue={setValue} />
      </TabPanel>
      <TabPanel value={value} setValue={setValue} index={1}>
        <HTS value={value} setValue={setValue} />
      </TabPanel>
      <TabPanel value={value} setValue={setValue} index={2}>
        <Biometrics value={value} setValue={setValue} />
      </TabPanel>
      <TabPanel value={value} setValue={setValue} index={3}>
        <DownloadNdr value={value} setValue={setValue} setErrors={setErrors} />
      </TabPanel>
      <TabPanel value={value} setValue={setValue} index={4}>
        <ErrorPage value={value} setValue={setValue} errors={errors} />
      </TabPanel>
      <TabPanel value={value} setValue={setValue} index={5}>
        <NdrConfiguration value={value} setValue={setValue} />
      </TabPanel>
      <TabPanel value={value} setValue={setValue} index={6}>
        <Redact value={value} setValue={setValue} />
      </TabPanel>
    </div>
  );
};

export default HomePage;
