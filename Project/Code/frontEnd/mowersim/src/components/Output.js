import React, { Component } from 'react';
import { Segment, SegmentGroup, Container } from 'semantic-ui-react';
import OverflowScrolling from 'react-overflow-scrolling';
import './style.css'

class Output extends Component {
    constructor(props) {
        super(props);
        this.state = {}
    }
    render() {
        return (
            <Container >
                {this.props.outputs.map((output, index) => {
                    return(                   
                    <Segment className='wrapText'>{output}</Segment>
                    )
                })}
            </Container>
            
            
        )
    }
}
export default Output;