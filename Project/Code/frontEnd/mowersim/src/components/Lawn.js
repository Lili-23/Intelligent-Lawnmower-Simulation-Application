import React, { Component } from 'react'
import { Table, TableBody, TableRow, TableCell, Label,Image } from 'semantic-ui-react'

import GRASS from '../picture/GRASS.png';
import GOPHER_EMPTY from '../picture/GOPHER_EMPTY.png';
import GOPHER_GRASS from '../picture/GOPHER_GRASS.png';
import MOWER_EAST from '../picture/MOWER_EAST.png';
import MOWER_NORTH from '../picture/MOWER_NORTH.png';
import MOWER_NORTHEAST from '../picture/MOWER_NORTHEAST.png';
import MOWER_NORTHWEST from '../picture/MOWER_NORTHWEST.png';
import MOWER_SOUTH from '../picture/MOWER_SOUTH.png';
import MOWER_SOUTHEAST from '../picture/MOWER_SOUTHEAST.png';
import MOWER_SOUTHWEST from '../picture/MOWER_SOUTHWEST.png';
import MOWER_WEST from '../picture/MOWER_WEST.png';
import EMPTY from '../picture/EMPTY.png';
import EMPTY_CHARGING from '../picture/EMPTY_CHARGING.png';
import GOPHER_EMPTY_CHARGING from '../picture/GOPHER_EMPTY_CHARGING.png';
import MOWER_EAST_CHARGING from '../picture/MOWER_EAST_CHARGING.png'
import MOWER_NORTH_CHARGING from '../picture/MOWER_NORTH_CHARGING.png'
import MOWER_NORTHEAST_CHARGING from '../picture/MOWER_NORTHEAST_CHARGING.png'
import MOWER_NORTHWEST_CHARGING from '../picture/MOWER_NORTHWEST_CHARGING.png'
import MOWER_SOUTH_CHARGING from '../picture/MOWER_SOUTH_CHARGING.png'
import MOWER_SOUTHEAST_CHARGING from '../picture/MOWER_SOUTHEAST_CHARGING.png'
import MOWER_SOUTHWEST_CHARGING from '../picture/MOWER_SOUTHWEST_CHARGING.png'
import MOWER_WEST_CHARGING from '../picture/MOWER_WEST_CHARGING.png'

function addImage(info) {
    switch (info) {
        case 'EMPTY':
            return <Image src={EMPTY} width="38" height="38"/>
        case 'GRASS':
            return <Image src={GRASS} width="38" height="38" />
        case 'GOPHER_EMPTY':
            return <Image src={GOPHER_EMPTY} width="38" height="38"/>
        case 'GOPHER_GRASS':
            return <Image src={GOPHER_GRASS} width="38" height="38"/>
        case 'MOWER_EAST':
            return <Image src={MOWER_EAST} width="38" height="38"/>
        case 'MOWER_NORTH':
            return <Image src={MOWER_NORTH} width="38" height="38"/>
        case 'MOWER_NORTHEAST':
            return <Image src={MOWER_NORTHEAST} width="38" height="38"/>
        case 'MOWER_NORTHWEST':
            return <Image src={MOWER_NORTHWEST} width="38" height="38"/>
        case 'MOWER_SOUTH':
            return <Image src={MOWER_SOUTH} width="38" height="38"/>
        case 'MOWER_SOUTHEAST':
            return <Image src={MOWER_SOUTHEAST} width="38" height="38"/>
        case 'MOWER_SOUTHWEST':
            return <Image src={MOWER_SOUTHWEST} width="38" height="38"/>
        case 'MOWER_WEST':
            return <Image src={MOWER_WEST} width="38" height="38"/>

        case 'EMPTY_CHARGING':
            return <Image src={EMPTY_CHARGING} width="38" height="38"/>
        case 'GOPHER_EMPTY_CHARGING':
            return <Image src={GOPHER_EMPTY_CHARGING} width="38" height="38"/>
        case 'MOWER_EAST_CHARGING':
            return <Image src={MOWER_EAST_CHARGING} width="38" height="38"/>
        case 'MOWER_NORTH_CHARGING':
            return <Image src={MOWER_NORTH_CHARGING} width="38" height="38"/>
        case 'MOWER_NORTHEAST_CHARGING':
            return <Image src={MOWER_NORTHEAST_CHARGING} width="38" height="38"/>
        case 'MOWER_NORTHWEST_CHARGING':
            return <Image src={MOWER_NORTHWEST_CHARGING} width="38" height="38"/>
        case 'MOWER_SOUTH_CHARGING':
            return <Image src={MOWER_SOUTH_CHARGING} width="38" height="38"/>
        case 'MOWER_SOUTHEAST_CHARGING':
            return <Image src={MOWER_SOUTHEAST_CHARGING} width="38" height="38"/>
        case 'MOWER_SOUTHWEST_CHARGING':
            return <Image src={MOWER_SOUTHWEST_CHARGING} width="38" height="38"/>
        case 'MOWER_WEST_CHARGING':
            return <Image src={MOWER_WEST_CHARGING} width="38" height="38"/>
        default:
            return null
    }
}
class Lawn extends Component {
    constructor(props) {
        super(props)
    }
    
    render() {
      return (
          <Table celled style={{width:"100%",height:"80%",marginTop:"10px"}} size="large">
              <TableBody>
                 {this.props.currentLawn.map((square, index) => {
                     return (
                        <TableRow>
                            {square.map((info, key) => {
                                return <TableCell >{addImage(info)}</TableCell>
                            })}
                        </TableRow>
                     )
                 })}
              </TableBody>
          </Table>
      )
    }
}
export default Lawn