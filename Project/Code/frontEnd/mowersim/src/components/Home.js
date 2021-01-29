import React, { Component } from 'react'
import { Container, Segment, Header, SegmentGroup, Button, Icon,Image } from 'semantic-ui-react'
import initFile from '../data/turnInfo'
import Lawn from './Lawn';
import FileInput from './FileInput';
import MowerInfo from './MowerInfo';
import Output from './Output';
import OverflowScrolling from 'react-overflow-scrolling';
import COMPASS from '../picture/COMPASS.PNG'
import './style.css'
class Home extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentLawn: [],
            mowerEnergy: 0,
            currentTurn: 0,
            maxTurn: 0,
            totalGrass: 0,
            remainGrass: 0,
            mowerNumber: 0,
            gopherNumber: 0,
            stopFlag: false,
            activeStart: false,
            activePause: false,
            showFinal: false,
            lawnHeight: 0,
            lawnWidth: 0,
            outputAction: [],
            gameState: '',
            mowersInfo:[],
            
        }
    }

   handleNextClick = () => {
        fetch("http://localhost:8080/api/simulate/next")
        .then(res => res.json())
        .then( result => {
            this.setState({
                currentLawn: result.currentLawn,
                currentTurn: result.currentTurn,
                totalGrass: result.totalGrass,
                remainGrass: result.remainGrass,
                maxTurn: result.turnLimit,
                mowerNumber: result.mowerNumber,
                gopherNumber: result.gopherNumber,
                outputAction:[result.message,...this.state.outputAction],
                mowersInfo: result.mowerStateInfo
            })
        },
            error => {
                console.log(error)
            }
        )
   }
  
   handleFastClick=() => {
       this.interval = setInterval(() => {
        fetch("http://localhost:8080/api/simulate/next")
            .then(res => res.json())
            .then( result => {
                console.log(result)
                this.setState(() => ({
                    currentLawn: result.currentLawn,
                    currentTurn: result.currentTurn,
                    totalGrass: result.totalGrass,
                    remainGrass: result.remainGrass,
                    maxTurn: result.turnLimit,
                    mowerNumber: result.mowerNumber,
                    gopherNumber: result.gopherNumber,
                    outputAction:[result.message,...this.state.outputAction],
                    mowersInfo: result.mowerStateInfo,
                    gameState:result.gameState
                }),() => {
                    if(this.state.gameState ==='END') {
                        console.log("stop here")
                        this.stopFastForward()
                    }
                })
            },
                error => {
                    console.log(error)
                }
        )
       },300)
       
   }

   stopFastForward = () => {
    clearInterval(this.interval); // Not working
    this.setState({stopFlag: !this.state.stopFlag})
    }

   handlePauseClick=() =>{
       this.stopFastForward();
       fetch("http://localhost:8080/api/simulate/quit")
       .then(res => res.json())
       .then(result => {
           console.log(result)
            this.setState({
                lawnHeight:result.lawnHeight,
                lawnWidth:result.lawnWidth,
                totalGrass: result.totalGrass,
                remainGrass: result.remainGrass,
                currentTurn:result.currentTurn,
                activePause: true,
                showFinal: !this.state.showFinal,
                stopFlag : !this.state.stopFlag,
                gameState: (this.state.gameState === 'END') ? 'RUNNING':'END'
            })
       },
        error => {
            console.log(error)
        }
       )
   }

    handleStartClick=() =>{
        fetch("http://localhost:8080/api/load")
            .then(res => res.json())
            .then(result => {
                    this.setState({
                        currentLawn: result.currentLawn,
                        currentTurn: result.currentTurn,
                        totalGrass: result.totalGrass,
                        remainGrass: result.remainGrass,
                        maxTurn: result.turnLimit,
                        mowerNumber: result.mowerNumber,
                        gopherNumber: result.gopherNumber,
                        outputAction:[result.message,...this.state.outputAction],
                        mowersInfo: result.mowerStateInfo
                    })
                },
                error => {
                    console.log(error)
                }
            )
    }

    render() {
        
        return (
            <Container style={{width:"95%",height:"60%"}}>
                <Header size='huge'>Welcome To Team A7-10</Header>  
               <SegmentGroup horizontal>
                    <Segment style={{width:"10%",border:"hidden"}}><Image src={COMPASS} style={{width:"100px",height:"100px"}}/></Segment>
                    <Segment style={{width:"90%",border:"hidden"}}><FileInput style={{height:"100px"}}/></Segment>
                   
               </SegmentGroup>
               <SegmentGroup horizontal >
                    <Segment style={{width:"70%",height:"60%"}} >
                        <Lawn currentLawn={this.state.currentLawn} />
                    </Segment>
                    <Segment style={{width:"30%"}}>
                        <SegmentGroup horizontal>
                            <SegmentGroup style={{marginTop:"10px"}}>
                                <Segment>Current Turn: {this.state.currentTurn}</Segment>
                                <Segment>Number of Mowers: {this.state.mowerNumber}</Segment>
                                <Segment>Number of Gophers: {this.state.gopherNumber}</Segment>
                                <Segment>Max Turns: {this.state.maxTurn}</Segment>
                                <Segment>Total Grass: {this.state.totalGrass}</Segment>
                                <Segment>Remain Grass: {this.state.remainGrass}</Segment>
                            </SegmentGroup>
                            <OverflowScrolling className='overflow-scrolling'>
                                 <Output outputs={this.state.outputAction} />
                            </OverflowScrolling>
                        </SegmentGroup>
                        <Segment><MowerInfo mowersInfo={this.state.mowersInfo}/></Segment>

                    </Segment>
                   
               </SegmentGroup>
               <SegmentGroup horizontal>
                   <Segment>
                       <Button icon labelPosition='right' onClick={this.handleStartClick}>
                           <Icon name='play' />
                           Start
                       </Button>
                   </Segment>
                   <Segment>
                        <Button icon labelPosition='right' onClick={this.handlePauseClick}>
                            <Icon name='pause' />    
                            Pause
                        </Button>
                   </Segment>
                   <Segment>
                        <Button icon labelPosition='right' color="blue" onClick={this.handleNextClick}>
                            Next
                            <Icon name='right arrow' />
                        </Button>
                   </Segment>
                   <Segment>
                        <Button icon labelPosition='right' color="red" onClick={this.handleFastClick}>
                            Fast-Forward
                            <Icon name='fast forward' />
                        </Button>
                   </Segment>
               </SegmentGroup>
               {this.state.showFinal ? <SegmentGroup horizontal>
               <Segment>The size of Lawn: {this.state.lawnHeight * this.state.lawnWidth}</Segment>
                   <Segment> The number of grass squares that have been cut so far: {this.state.totalGrass - this.state.remainGrass}</Segment>
                   <Segment> The number of grass squares remaining: {this.state.remainGrass}</Segment>
                   <Segment>The number of turns taken so far: {this.state.currentTurn}</Segment>
               </SegmentGroup>: null}
            </Container>
        )
    }
}
export default Home