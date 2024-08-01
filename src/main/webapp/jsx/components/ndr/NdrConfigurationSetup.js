import React, {useState, useEffect} from 'react';
import { Modal, ModalHeader, ModalBody,Form,FormFeedback,
Row,Col, Card,CardBody, FormGroup, Input, Label} from 'reactstrap';
import Button from '@material-ui/core/Button'
import { makeStyles } from '@material-ui/core/styles'

import { Spinner } from 'reactstrap';
import axios from "axios";
import {  toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { token as token,  url as baseUrl } from "./../../../api";


const useStyles = makeStyles(theme => ({
    card: {
        margin: theme.spacing(20),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(3)
    },
    submit: {
        margin: theme.spacing(3, 0, 2)
    },
    cardBottom: {
        marginBottom: 20
    },
    Select: {
        height: 45,
        width: 350
    },
    button: {
        margin: theme.spacing(1)
    },

    root: {
        '& > *': {
            margin: theme.spacing(1)
        }
    },
    input: {
        display: 'none'
    },
    error: {
        color: "#f85032",
        fontSize: "11px",
    },
    success: {
        color: "#4BB543 ",
        fontSize: "11px",
    }, 
}))



const DatabaseSyn = (props) => {
    const classes = useStyles()
    const defaultValues = { username: "", password: ""  }
    //console.log(props)
    const [patDetails, setPatDetails] = useState(defaultValues);
    const [saving, setSaving] = useState(false);
    const [errors, setErrors] = useState({});
    
    const handleInputChange = e => {
      setPatDetails ({...patDetails,  [e.target.name]: e.target.value});
    }
    /*****  Validation */
    const validate = () => {
    let temp = { ...errors };
    temp.username = patDetails.username
        ? ""
        : "Username is required";
        temp.password = patDetails.password
        ? ""
        : "Password is required";
        
        setErrors({
            ...temp,
        });
        return Object.values(temp).every((x) => x === "");
    };

    const handleSubmit = (e) => {
      e.preventDefault();
            if (validate()) {      
                    setSaving(true);
                    axios.post(`${baseUrl}ndr-emr/auto-push-configuration?username=${patDetails.username}&password=${patDetails.password}`,patDetails,
                     { headers: {"Authorization" : `Bearer ${token}`}},
                    
                    ).then(response => {
                            setSaving(false);
                            props.NdrSetup()
                            toast.success("NDR Setup Successful");
                            props.toggleModal()

                    }).catch(error => {
                            setSaving(false);
                            if(error.response && error.response.data){
                                let errorMessage = error.response.data.apierror && error.response.data.apierror.message!=="" ? error.response.data.apierror.message :  "Something went wrong, please try again";
                                
                                    toast.error(errorMessage, {position: toast.POSITION.BOTTOM_CENTER});
                                
                            }
                            else{
                                toast.error("Something went wrong. Please try again...", {position: toast.POSITION.BOTTOM_CENTER});
                            }
                        
                        });
            }
        }

      
  return (      
      <div >
         
              <Modal isOpen={props.showModal} toggle={props.toggleModal} className={props.className} size="lg" backdrop={false} backdrop="static">
              <Form >
             <ModalHeader toggle={props.toggleModal}>NDR Setup</ModalHeader>
                <ModalBody>
                    
                    <Card >
                        <CardBody>
                            <Row >
                            <Col md={12}>
                            <FormGroup>
                            <Label >Username </Label>
                                    <Input
                                        type="text"
                                        name="username"
                                        id="username" 
                                        value={patDetails.username}
                                        onChange={handleInputChange}
                                        required
                                        />
                                    {errors.username !=="" ? (
                                        <span className={classes.error}>{errors.username}</span>
                                    ) : "" }
                            </FormGroup>
                            </Col>
                            <Col md={12}>
                            <FormGroup>
                            <Label >Password </Label>
                                    <Input
                                        type="password"
                                        name="password"
                                        id="password" 
                                        value={patDetails.password}
                                        onChange={handleInputChange}
                                        required
                                        />
                                    {errors.password !=="" ? (
                                        <span className={classes.error}>{errors.password}</span>
                                    ) : "" }
                            </FormGroup>
                            </Col>                    
                            </Row>
                            {saving ? <Spinner /> : ""}
                            <br/>              
                            <Button
                                type='submit'
                                variant='contained'
                                color='primary'
                                
                                //startIcon={<SettingsBackupRestoreIcon />}
                                onClick={handleSubmit}
                                
                            >   
                                <span style={{ textTransform: "capitalize " }}>Connect & Save</span>  
                            </Button>
                        </CardBody>
                        </Card> 
                    </ModalBody>
        
                </Form>
      </Modal>
    </div>
  );
}

export default DatabaseSyn;
